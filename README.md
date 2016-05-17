# NodeData

Example 1:
```java
public void example1(File jsonFile) {
  NodeAdapter jsonAdapter = NodeAdapter.json();
  NodeObject data = jsonAdapter.from(jsonFile).asNodeObject();
  System.out.println(data.get("some_key").asString());
}
```

Example 2:
```java
public void example2(URL jsonData) {
  NodeAdapter customAdapter = NodeAdapter.builder().readJson().writeHocon().build();
  Node data = customAdapter.from(jsonData);
  File hoconFile = new File(jsonFile.getParentFile(), "output.hocon");
  customAdapter.to(data, hoconFile);
}
```
