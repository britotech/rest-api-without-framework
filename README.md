## API REST em java puro sem nenhum framework

Este é um aplicativo de demonstração desenvolvido em Java 17 usando modulo
[`jdk.httpserver`](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.httpserver/com/sun/net/httpserver/package-summary.html) 
e algumas bibliotecas Java adicionais como [vavr](http://www.vavr.io/) por exemplo.

## Motivação do projeto
Hoje em dia o padrão para criação de uma API REST java é baseado na utilização do framework Spring.
Após ver um questionamento no twitter sobre como as pessoas estão se tornando especialistas em frameworks e não na solução
de problemas resolvi me desafiar e contruir a api sem a utilização do Spring.

## Primeiro endpoint

O ponto de partida da aplicação web é `com.sun.net.httpserver.HttpServer` class. 
O mais simples endpoint `/api/status` poderia ser como abaixo: 

```java
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Application {

    public static void main(String[] args) throws IOException {
        var server = HttpServer.create(new InetSocketAddress(8080), 0);
        var statusContext = server.createContext("/api/status", (exchange -> processRequest(exchange)));

        server.setExecutor(null);
        server.start();
    }

    private static void processRequest(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            var responseText = "It's fine!";
            exchange.sendResponseHeaders(200, responseText.getBytes().length);
            var output = exchange.getResponseBody();
            output.write(responseText.getBytes());
            output.flush();
        } else {
            exchange.sendResponseHeaders(405, -1);
        }

        exchange.close();
    }
}
```
Quando você executa o programa principal, ele inicia o servidor web na porta `8080` e expõe o primeiro endpoint que está apenas imprimindo `It's fine!`, por exemplo usando  curl:

```bash
curl localhost:8080/api/status
```
Nosso endpoint ao ser acionado por um método GET ele nos retorna a mensagem espera, e qualquer outro tipo de método é retornado um erro 405 como no próximo exemplo:
```bash
curl -v -X POST localhost:8080/api/status
```


```bash
> POST /api/status HTTP/1.1
> Host: localhost:8000
> User-Agent: curl/7.83.1
> Accept: */*
> 
< HTTP/1.1 405 Method Not Allowed
```

## Segurança para nosso endpoint
Um caso comum em cada API REST é proteger alguns endpoints com credenciais, por exemplo, usando autenticação básica.
Para cada contexto de servidor podemos definir um autenticador conforme abaixo:

```java
import com.sun.net.httpserver.BasicAuthenticator;

public class ApiAuthenticator extends BasicAuthenticator {

    public ApiAuthenticator() {
        super("realm");
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        return username.equals("admin") && password.equals("admin");
    }
}
```
Agora só vincularmos o nosso ApiAuthenticator ao context que foi criado.
```java
statusContext.setAuthenticator(new ApiAuthenticator());
```

Agora você pode invocar este endpoint protegido adicionando um cabeçalho `Authorization` como este:

```bash
curl -v localhost:8080/api/status -H 'Authorization: Basic YWRtaW46YWRtaW4='
```

O texto após `Basic` é um `admin:admin` codificado em Base64, que são credenciais codificadas em nosso código de exemplo. Em produção isso seria consultado e comparado com registros do banco de dados, mais tarde vamos melhorar isso.

## JSON, manipuladores de exceção e outros

Agora vamos criar um novo endpoint na API para cadastrar novos usuários. Usaremos um banco de dados na memória para armazená-los.

```java
import java.util.UUID;

public class User  {
    
    private UUID id;
    private String username;
    private String password;    
    
    // GETS SETTERS EQUALS HASCODE
}

```
Agora criamos um objeto que será responsável pelos dados da requisição

```java
public class UserRegistrationRequest {

    private String username;
    private String password;    
}
```

Os usuários serão criados em um serviço que usarei no meu manipulador de API. O método de serviço é simplesmente armazenar o usuário.

A implementação na memória do repositório é a seguinte:
```java

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepositoryImpl implements UserRepository {

    private static final Map<String, User> USERS_STORE = new ConcurrentHashMap();

    @Override
    public User create(User user) {
        user.setId(UUID.randomUUID());
        USERS_STORE.put(user.getUsername(), user);
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return USERS_STORE.get(username);
    }
}
```
Finalmente vamos ao nosso handler. A classe com toda implementação estará disponvível no repositório do projeto.

```java
    @Override
    protected void execute(HttpExchange exchange) throws IOException {
        if(!requestMethodIsPost().test(exchange)){
            throw ApplicationExceptions.methodNotAllowed(formatErrorMessage(exchange)).get();
        }

        ResponseEntity responseEntity = doPost(exchange.getRequestBody());
        configureResponse(exchange, responseEntity);
    }

    private ResponseEntity doPost(InputStream inputStream) {
        var userRequest = super.readRequest(inputStream, UserRegistrationRequest.class);

        if (isNull(userRequest.getUsername())) {
            return getReponseEntityBadRequest("Username is invalid!");
        }

        if (isNull(userRequest.getPassword())) {
            return getReponseEntityBadRequest("Password is invalid!");
        }

        if (userService.findByUsername(userRequest.getUsername()).isPresent()) {
            return getReponseEntityBadRequest("Username is already taken!");
        }

        var user = userService.create(new User(userRequest.getUsername(), PasswordEncoder.encode(userRequest.getPassword())));
        var userResponse = new UserRegistrationResponse(user.getId(), user.getUsername());
        return new ResponseEntity<>(userResponse, getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.CREATED);
    }
```

Ele converte a solicitação JSON em `UserRegistrationRequest` que definimos acima.

eu preciso converter `UserRegistrationResponse` objeto para JSON string.

O JSON de empacotamento e desempacotamento é feito com o mapeador de objetos Jackson (`com.fasterxml.jackson.databind.ObjectMapper`).

E é assim que instanciamos o novo manipulador no método main do aplicativo:

```java
 server.createContext("/api/users/register", getUserRegistrationHandler()::handle);

```

You can run the application and try one of the example requests below: 

```bash
curl -X POST localhost:8080/api/users/register -d '{"username": "test" , "password" : "test"'
```

response: 
```bash
{"id":"9cb2b902-9d14-4b85-9b53-a76fa63b28c8", "username":"test"}
```

```bash
curl -v -X POST localhost:8080/api/users/register -d '{"wrong": "request"}'
```

response: 
```bash
< HTTP/1.1 400 Bad Request
< Date: Sat, 29 Dec 2018 00:11:21 GMT
< Transfer-encoding: chunked
< Content-type: application/json
< 
* Connection #0 to host localhost left intact
{"code":400,"message":"Unrecognized field \"wrong\" (class com.consulner.app.api.user.RegistrationRequest), not marked as ignorable (2 known properties: \"login\", \"password\"])\n at [Source: (sun.net.httpserver.FixedLengthInputStream); line: 1, column: 21] (through reference chain: com.consulner.app.api.user.RegistrationRequest[\"wrong\"])"}
```
Criei mais alguns endpoints para brincar um pouco, alguns onde precisei fazer a leitura dos parametros informado na requisição.

/api/users/register -> PUT - Para registros de usuários

/api/status -> GET - Obtém o status do serviço.

/api/customers -> GET devolve os clientes cadastrados

/api/customers/{id da cliente}/accounts -> GET lista as contas de um cliente específico

/api/accounts -> PUT Para criar uma nová conta bancária

/api/accounts/{id da conta}/withdraw PUT Para realizar um saque em uma conta bancária.






