# Tarcius
## Auditoria de atividade

### Objetivo
Tarcius � um componente que prov� uma API simplificada para registro de auditoria das atividades dos usu�rios em uma aplica��o.

Baseado nos conceitos e caracter�sticas disponibilizadas pelo CDI, possibilita o registro de chamadas a m�todos, bem como dos valores dos par�metros identificados, atribuindo refer�ncias que visam dar sentido de neg�cio �s informa��es coletadas.

Concebido para ser adapt�vel �s necessidades e particularidades de cada aplica��o, delega ao usu�rio do componente a defini��o do modelo da auditoria, a composi��o dos dados coletados no modelo definido e o envio desses ao reposit�rio escolhido para os dados de auditoria, al�m de possibilitar a defini��o de tradutores espec�ficos para os par�metros ou o uso de tradutores padr�es disponibilizados.

### Modelo de uso
O mecanismo baseia-se na intercepta��o da chamada de m�todos identificados com a anota��o *@Audit* atrav�s de um *interceptor* CDI que deve ser ativado com a inclus�o do seguinte trecho no arquivo beans.xml:

```xml
<interceptors>
    <class>com.github.ldeitos.tarcius.audit.interceptor.AuditInterceptor</class>
</interceptors>
```

Cabe a observa��o de que *interceptors* do CDI somente s�o acionados quando um m�todo p�blico � invocado por outra classe, mas nunca quando esta chamada � realizada internamente da pr�pria classe que tenha o m�todo anotado. Para maiores informa��es sobre *interceptors* do CDI consulte a [documenta��o oficial](https://docs.oracle.com/javaee/6/tutorial/doc/gkhjx.html).

O processo de auditoria realizada pelo *interceptor* divide-se em quatro fases:

1. Identifica��o dos par�metros a serem auditados
2. Tradu��o dos par�metros
3. Formata��o dos dados coletados
4. Envio dos dados formatados para o reposit�rio de auditoria

#### Identifica��o dos par�metros a serem auditados
Nesta fase s�o identificados todos os par�metros do m�todo que estejam identificados com a anota��o *@Audited* para posterior tradu��o.

#### Tradu��o dos par�metros para *String*
Nesta fase � aplicada a tradu��o dos par�metros anteriormente identificados para *String*, e podem ser dos seguintes tipos:

```java
TranslateType.STRING_VALUE
```

```java
TranslateType.JAXB_XML
```

```java
TranslateType.JAXB_FORMATTED_XML
```

```java
TranslateType.JAXB_JSON
```

```java
TranslateType.CUSTOM
```