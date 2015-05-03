# tengi - Realtime Communication Foundation

tengi is designed as a realtime communication platform between different languages like Java, C#, HTML5 and
ActionScript. Other client or server implementations are also possible. Currently Java is the only one implemented but
other named (at least) clients will follow.

tengi for Java requires Java 8 but fully utilizes the potential of lambda and newly integrated features.

## Code example

This is a short lookout into the currently proposed API.

Data Model:

```java
class User {
  private final int userId;
  private final String name;

  public User(int userId, String name) {
    this.userId = userId;
    this.name = name;
  }

  @Override public String toString() {
    return "User(userId=" + userId + ", name=" + name + ")";
  }

  public static void write(Object value, WritableMemoryBuffer memoryBuffer,
                           Protocol protocol) {
    User user = (User) value;
    memoryBuffer.writeInt(user.userId);
    memoryBuffer.writeString(user.name);
  }

  public static User read(ReadableMemoryBuffer memoryBuffer, Protocol protocol) {
    int userId = memoryBuffer.readInt();
    String name = memoryBuffer.readString();
    return new User(userId, name);
  }

  public static MarshallerFilter.Result isSerializable(Object value) {
    if (object instanceof User) {
      return MarshallerFilter.Result.AcceptedAndCache;
    }
    return MarshallerFilter.Result.Next;
  }
}
```

Client:

```java
class MyClient {
  public static void main(String[] args) throws Exception {
    // Create configuration using Builder
    Configuration configuration = new Configuration.Builder()

      // Configure a custom Marshaller (built-in ones are always activated)
      .addMarshaller(User::isSerializable, User::read, User::write)

      // Configure available transports
      .addTransport(ClientTransport.TCP_TRANSPORT,
          ClientTransport.RDP_TRANSPORT)

      // Configure transport ports
      .transportPort(ClientTransport.TCP_TRANSPORT, 8080)
      .transportPort(ClientTransport.RDP_TRANSPORT, 9090)

      // Build final configuration
      .build();

    // Create client instance using configuration
    Client client = Client.create(configuration);

    // Connect to remote server
    client.connect("127.0.0.1", MyClient::onConnection);
  }

  private static void onConnection(Connection connection) {
    // Add MessageListener with echo functionality
    connection.addMessageListener(
        (connection, message) -> connection.sendMessage(message));
  }

  private static void onMessage(Connection connection, Message message) {
    // Retrieve user login
    User user = message.getBody();

    // Build another message
    Message message = Message.create("Hello World " + user.toString());

    // Send the message to the server
    connection.writeMessage(message);
  }
}
```

Server:

```java
class MyServer {
  public static void main(String[] args) throws Exception {
    // Create configuration using Builder
    Configuration configuration = new Configuration.Builder()

      // Configure a custom Marshaller (built-in ones are always activated)
      .addMarshaller(User::isSerializable, User::read, User::write)

      // Configure available transports
      .addTransport(ServerTransport.TCP_TRANSPORT,
          ServerTransport.WEBSOCKET_TRANSPORT,
          ServerTransport.HTTP2_TRANSPORT,
          ServerTransport.RDP_TRANSPORT,
          ServerTransport.HTTP_TRANSPORT)

      // Configure transport ports
      .transportPort(ServerTransport.TCP_TRANSPORT, 8080)
      .transportPort(ServerTransport.WEBSOCKET_TRANSPORT, 8080)
      .transportPort(ServerTransport.HTTP2_TRANSPORT, 8080)
      .transportPort(ServerTransport.RDP_TRANSPORT, 9090)
      .transportPort(ServerTransport.HTTP_TRANSPORT, 8080)

      // Build final configuration
      .build();

    // Create server instance using configuration
    Server server = Server.create(configuration);

    // Start server and wait for client connections
    server.start(MyServer::onConnection);
  }

  private static void onConnection(Connection connection) {
    // Add MessageListener with echo functionality
    connection.addMessageListener(
        (connection, message) -> System.out.println(message.getBody());

    // Create fake user
    User user = new User(1, "Peter");

    // Send fake user login
    connection.sendObject(user);
  }
}
```
