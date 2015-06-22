# Tarcius
## Auditoria de atividade

### Objetivo
Tarcius � um componente que prov� uma API simplificada para registro de auditoria das atividades dos usu�rios em uma aplica��o.

Baseado nos conceitos e caracter�sticas disponibilizadas pelo CDI, possibilita o registro de chamadas a m�todos, bem como dos valores dos par�metros identificados, atribuindo refer�ncias que visam dar sentido de neg�cio �s informa��es coletadas.

Concebido para ser adapt�vel �s necessidades e particularidades de cada aplica��o, delega ao usu�rio do componente a defini��o do modelo da auditoria, a composi��o dos dados coletados no modelo definido e o envio desses ao reposit�rio escolhido para os dados de auditoria, al�m de possibilitar a defini��o de tradutores espec�ficos para os par�metros ou o uso de tradutores padr�es disponibilizados.
