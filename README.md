# TadeuBooter

**TadeuBooter** é uma biblioteca poderosa desenvolvida para facilitar a manipulação de dados e processos em projetos Java, com funcionalidades úteis para aplicações de Minecraft, manipulação de JSON, serialização de dados e muito mais. Com uma API intuitiva, ela visa acelerar o desenvolvimento ao oferecer soluções prontas para desafios comuns em aplicações de software.

## Recursos Principais

- Manipulação e serialização de inventários e itens em Minecraft.
- Ferramentas para manipulação de arquivos JSON.
- Classes utilitárias como `FancyTime`, `ItemBuilder`, `Cuboid` e **muito mais** para maior produtividade.
- Suporte à integração com HTTP.

---

## Como Adicionar ao Seu Projeto

TadeuBooter está hospedado no [JitPack](https://jitpack.io/), o que torna sua integração simples tanto para projetos Maven quanto Gradle.

### Maven

Adicione o repositório JitPack ao seu arquivo `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Em seguida, adicione a dependência:

```xml
<dependency>
    <groupId>com.github.zyypj</groupId>
    <artifactId>tadeuBooter</artifactId>
    <version>1.0</version>
</dependency>
```

### Gradle

Adicione o repositório JitPack no bloco `repositories` do seu `build.gradle`:

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
```

Adicione a dependência no bloco `dependencies`:

```gradle
dependencies {
    implementation 'com.github.zyypj:tadeuBooter:1.0'
}
```

---

## Primeiros Passos

1. Importe as classes necessárias no seu projeto.
2. Utilize os recursos oferecidos pela biblioteca para resolver problemas comuns de manipulação de dados.

Por exemplo, para serializar um inventário no Minecraft:

```java
Serializer serializer = new Serializer();
String serializedInventory = serializer.serializeInventory(inventoryType, inventory);
```

E para desserializar:

```java
Inventory deserializedInventory = serializer.deserializeInventory(serializedInventory);
```

---

## Agradecimentos

Obrigado ao [**syncwrld**](https://github.com/syncwrld) pela disponibilidade de alguns códigos.

---

## Contribuições e Feedback

Contribuições são bem-vindas! Se você encontrar problemas ou tiver ideias para melhorias, sinta-se à vontade para abrir uma [issue](https://github.com/zyypj/tadeuBooter/issues) ou enviar um [pull request](https://github.com/zyypj/tadeuBooter/pulls).

---

## Links úteis

- [JitPack - TadeuBooter](https://jitpack.io/#zyypj/tadeuBooter/v0.5)
- [Repositório no GitHub](https://github.com/zyypj/tadeuBooter)
