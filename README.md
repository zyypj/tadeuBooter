Aqui está o README atualizado com instruções para realizar o **relocate** no uso da biblioteca **TadeuBooter**, utilizando tanto Maven quanto Gradle. O exemplo de Gradle usa o plugin **Shadow**, conforme especificado.

---

# TadeuBooter

**TadeuBooter** é uma biblioteca poderosa desenvolvida para facilitar a manipulação de dados e processos em projetos Java, com funcionalidades úteis para aplicações de Minecraft, manipulação de JSON, serialização de dados e muito mais. Com uma API intuitiva, ela visa acelerar o desenvolvimento ao oferecer soluções prontas para desafios comuns em aplicações de software.

---

## Recursos Principais

- Manipulação e serialização de inventários e itens em Minecraft.
- Ferramentas para manipulação de arquivos JSON.
- Classes utilitárias como `ItemBuilder`, `ActionBarBuilder`, `Cuboid` e muito mais para maior produtividade.
- Suporte à integração com HTTP.
- Ajuda em database

---

## Como Adicionar ao Seu Projeto

### 1. Configurando Dependências

TadeuBooter está hospedado no [JitPack](https://jitpack.io/), tornando sua integração simples.

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

Adicione a dependência:

```xml
<dependency>
    <groupId>com.github.zyypj</groupId>
    <artifactId>tadeuBooter</artifactId>
    <version>1.2.5.2</version>
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
    implementation 'com.github.zyypj:tadeuBooter:1.2.5.2'
}
```

---

## 2. Relocate: OBRIGATÓRIO

### Relocate com Gradle (Shadow Plugin)

Siga o exemplo abaixo para configurar o **relocate** no Gradle usando o **Shadow Plugin**:

```gradle
plugins {
    id 'java'
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = 'com.github.zyypj'
version = '1.0'

repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.zyypj:tadeuBooter:1.2.5.2'
}

shadowJar {
    relocate 'com.github.zyypj.tadeuBooter', 'com.github.zyypj.lcteams.relocated.tadeuBooter'
    minimize()
}

tasks {
    build {
        dependsOn shadowJar
    }
}
```

> No exemplo acima:
> - O pacote `com.github.zyypj.tadeuBooter` foi movido para `com.github.zyypj.lcteams.relocated.tadeuBooter`.

### Relocate com Maven (Shade Plugin)

No Maven, utilize o **Shade Plugin** para realizar o relocate. Adicione o plugin ao seu `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.4.1</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <relocations>
                            <relocation>
                                <pattern>com.github.zyypj.tadeuBooter</pattern>
                                <shadedPattern>com.github.zyypj.lcteams.relocated.tadeuBooter</shadedPattern>
                            </relocation>
                        </relocations>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
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

---

# Lembrando, eu sou o tadeu :D
