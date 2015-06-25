# Tarcius
## Auditoria de atividade

### Objetivo
Tarcius � um componente que prov� uma API simplificada para registro de auditoria das atividades dos usu�rios em uma aplica��o.

Baseado nos conceitos e caracter�sticas disponibilizadas pelo CDI, possibilita o registro de chamadas a m�todos, bem como dos valores dos par�metros identificados, atribuindo refer�ncias que visam dar sentido de neg�cio �s informa��es coletadas.

Concebido para ser adapt�vel �s necessidades e particularidades de cada aplica��o, delega ao usu�rio do componente a defini��o do modelo da auditoria, a composi��o dos dados coletados no modelo definido e o envio desses ao reposit�rio escolhido para os dados de auditoria, al�m de possibilitar a defini��o de tradutores espec�ficos para os par�metros ou o uso de tradutores padr�es disponibilizados.

### Modelo de uso
O mecanismo baseia-se na intercepta��o da chamada de m�todos identificados com a anota��o *@Audit* atrav�s de um *interceptor* CDI que deve ser ativado no arquivo *beans.xml* conforme abaixo:

```xml
<interceptors>
    <class>com.github.ldeitos.tarcius.audit.interceptor.AuditInterceptor</class>
</interceptors>
```

Cabe a observa��o de que *interceptors* do CDI somente s�o acionados quando um m�todo p�blico � invocado por outra classe, mas nunca quando esta chamada � realizada internamente da pr�pria classe que tenha o m�todo anotado. Para maiores informa��es sobre *interceptors* do CDI consulte a [documenta��o oficial](https://docs.oracle.com/javaee/6/tutorial/doc/gkhjx.html).

A anota��o *@Audit* possibilita a especifica��o da refer�ncia do ponto de auditoria atrav�s do atributo *auditRef*. Esta refer�ncia � textual e pode ser utilizada para identificar o ponto de auditoria, mapeando-a para o dom�nio do neg�cio, por exemplo. Quando omitida esta configura��o o nome do m�todo interceptado � utilizado como refer�ncia. 

O processo de auditoria realizada pelo *interceptor* divide-se em quatro fases:

1. Identifica��o dos par�metros a serem auditados
2. Tradu��o dos par�metros
3. Formata��o dos dados coletados
4. Envio dos dados formatados para o reposit�rio de auditoria

#### Identifica��o dos par�metros a serem auditados
Nesta fase s�o identificados todos os par�metros do m�todo que estejam identificados com a anota��o *@Audited* para posterior tradu��o.
A anota��o pode ser aplicada aos par�metros a serem auditados no m�todo marcado para intercepta��o ou diretamente no *bean* que dever� ser auditado, conforme abaixo:

Identificando no par�metro
```java
public class VendasBC {

    @Audit(auditRef="consulta de vendas")
    public void consultar(@Audited(auditRef="par�metros aplicados na consulta") Parametro par){}

}
``` 

Identificando no bean
```java
@Audited(auditRef="par�metros aplicados na consulta") 
public class Parametro {

    ...

}
```
```java
public class VendasBC {

    @Audit(auditRef="consulta de vendas")
    public void consultar(Parametro par){}

}
```
Quando anotado no *bean*, sempre que esse for utilizado como par�metro em um m�todo interceptado ele ser� considerado como parte do conte�do a ser auditado. Entretanto, caso seja necess�rio ignorar em algum caso espec�fico um par�metro cuja classe est� anotada, basta identifica-lo com a anota��o *@NotAudited*, como abaixo:

```java
public class VendasBC {

    @Audit(auditRef="consulta de vendas")
    public void consultar(@NotAudited Parametro par){}

}
```
 
#### Tradu��o dos par�metros
Nesta fase � aplicada a tradu��o dos par�metros anteriormente identificados para *String*. Esta tradu��o pode ser resolvida por tradutores pr�-definidos e disponibilizados pelo componente ou ainda por outros customizados para casos espec�ficos. A configura��o do tradutor a ser aplicado se faz atrav�s do atributo *translator* da anota��o *@Audited*, podendo ter os seguintes valores:

```java
TranslateType.STRING_VALUE
```
Aplica o tradutor que resolve o valor do par�metro auditado para uma *String* representativa do objeto, atrav�s do m�todo String.*valueOf()*. Este tipo � o valor *default* do atributo.
```java
TranslateType.JAXB_XML
```
Aplica o tradutor que resolve o valor do par�metro auditado para o formato XML utilizando o padr�o da [API JAXB]( https://jaxb.java.net/). O XML resultante por este tradutor � apresentado em uma linha �nica, sem formata��o. 
```java
TranslateType.JAXB_FORMATTED_XML
```
Aplica o tradutor que resolve o valor do par�metro auditado para o formato XML utilizando o padr�o da [API JAXB]( https://jaxb.java.net/). Ao contr�rio do anterior, o resultado deste tradutor � o XML formatado.
```java
TranslateType.JAXB_JSON
```
Aplica o tradutor que resolve o valor do par�metro auditado para o formato JSON utilizando o componente [jersey-json]( https://jersey.java.net/documentation/1.19/index.html), extens�o da [API JAXB]( https://jaxb.java.net) que utiliza o mesmo mecanismo de anota��es para determinar o modelo do resultado. Para este formatador o resultado � retornado em uma linha �nica, sem formata��o.
```java
TranslateType.CUSTOM
```
Quando o atributo *translator* est� valorizado com este tipo, significa que a tradu��o do par�metro deve ser resolvida por um tradutor especializado. Quando utilizado este valor, � **obrigat�rio** informar no atributo *customResolverQualifier* com o qualificador *@CustomResolver* que identifica o tradutor a ser instanciado, atrav�s do CDI, para resolver o valor do par�metro. 

As implementa��es especializadas dos tradutores devem implementar a interface *ParameterResolver<I>*, al�m de ser devidamente qualificado com *@CustomResolver*, como no exemplo abaixo:
```java
@CustomResolver("resolve_pedido")
public class PedidoResolver implements ParameterResolver<Pedido> {

    public String resolve(Pedido input){...}

}
```
```java
public class VendasBC {

    @Audit(auditRef="Processar venda")
    public void processar(
       @Audited(auditRef="pedido", translator = TranslateType.CUSTOM, customResolverQualifier=@CustomResolver("resolve_pedido")) Pedido pedido){}

}
```
Cabe esclarecer que, apesar de obrigat�rio, a aus�ncia da configura��o do atributo *customResolverQualifier* n�o causa erro no processo de auditoria; neste caso, ser� aplicado o tradutor *default* para resolver o valor auditado obtendo, portanto, o valor do m�todo *toString()* do objeto.