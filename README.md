 <picture>
    <img src="https://i.imgur.com/cQruGU2.png" alt="GEST Logo">
  </picture>

---
<p align="center">
  <a href="#introdução">Introdução</a> —
  <a href="#requisitos">Requisitos</a> —
  <a href="#gestão-do-projeto">Gestão do Projeto</a> —
  <a href="#análise-e-projeto-do-software">Análise e Projeto do Software</a> —
  <a href="#conclusão">Conclusão</a>
</p>


---

## Introdução

O GEST (Gestão de core.Estoque) é um sistema desenvolvido com o objetivo de auxiliar no controle e gerenciamento de produtos em estoque de uma pequena empresa. O sistema permite o cadastro, consulta, atualização e remoção de produtos, bem como o registro de entradas e saídas, fornecendo relatórios que auxiliam na tomada de decisões relacionadas ao estoque, como produtos em falta, valor total armazenado e lucro estimado.

Inicialmente, o GEST foi concebido como um projeto acadêmico na disciplina de Programação Orientada a Objetos (POO), onde seu foco principal foi a modelagem do domínio do problema e a aplicação dos conceitos fundamentais de orientação a objetos, como encapsulamento, herança, polimorfismo e abstração.

<p align="center">
  <img src="https://i.imgur.com/NyAl882.png"/>
  <br>
  <em>Figura 1 – Versão POO</em>
</p>


Na disciplina de Processo de Desenvolvimento de Software (PDS), o sistema passa por um processo de expansão e aprimoramento, deixando de ser apenas uma aplicação conceitual para se tornar um sistema mais completo e próximo de um cenário real. O objetivo nesta nova etapa é desenvolver uma interface gráfica intuitiva para usuários finais, integrar o sistema a um banco de dados, além de aplicar práticas de engenharia de software, como metodologias ágeis, planejamento incremental e boas práticas de projeto.

<p align="center">
  <img src="https://i.imgur.com/PhtykJU.png"/>
  <br>
  <em>Figura 2 – Versão PDS (v1.0.0)</em> 
</p>

---

## Requisitos

Esta seção apresenta os requisitos funcionais e não-funcionais do sistema.

### Histórias do Projeto

**História 1 — Configuração do banco de dados (PostgreSQL)**  
*Como desenvolvedor, quero fazer a conexão com o banco de dados PostgreSQL, para garantir que os dados do sistema sejam salvos de forma persistente e segura.*  
**Status:** ✅ 

**História 2 — Criar tela inicial de navegação**  
*Como usuário, quero uma tela inicial gráfica com menus de navegação claros, para acessar facilmente as funcionalidades de estoque, vendas e relatórios sem fechar a aplicação.*  
**Status:** ✅

**História 3 — Criar tela de login e autenticação**  
*Como administrador, quero uma tela de autenticação para o sistema, para impedir que usuários não autorizados acessem ou modifiquem dados sensíveis.*  
**Status:** ✅

**História 4 — Criar tela para o cadastro de fornecedores**  
*Como gerente, quero um formulário gráfico para cadastrar fornecedores, para manter um registro organizado.*  
**Status:** ✅ 

**História 5 — Criar tela para o cadastro de produtos**  
*Como estoquista, quero uma tela para cadastrar novos produtos, para inserir novos itens no catálogo do sistema.*  
**Status:** ✅ 

**História 6 — Criar tela de movimentação de saída de estoque**  
*Como vendedor, quero uma tela para buscar um produto e lançar saídas, para atualizar o saldo do estoque em tempo real conforme as vendas acontecem.*  
**Status:** ✅ 

**História 7 — Criar listagem de contatos de fornecedores**  
*Como gerente, quero ver uma lista com todos os fornecedores e seus contatos, para facilitar a comunicação e consulta de dados quando necessário.*  
**Status:** ✅ 

**História 8 — Criar tabela de listagem de produtos**  
*Como estoquista, quero ver uma tabela com todos os produtos cadastrados, para conferir rapidamente o inventário disponível e suas características.*  
**Status:** ✅ 

**História 9 — Criar lançamento de itens em lote**  
*Como estoquista, quero lançar a entrada de múltiplos itens de uma só vez na mesma operação, para agilizar o processo de reposição.*  
**Status:** ✅ 

**História 10 — Criar campos dinâmicos por tipo de produto**  
*Como estoquista, quero uma tela que adapte os campos visíveis (ex: mostrar “Validade” apenas para perecíveis), para preencher apenas as informações relevantes para cada tipo de produto.*  
**Status:** ✅ 

**História 11 — Criar registro de logs de alterações**  
*Como administrador, quero que o sistema registre no banco quem realizou cada alteração importante, para ter rastreabilidade e controle sobre as ações dos usuários.*  
**Status:** ✅ 

**História 12 — Criar alertas visuais de situação crítica dos produtos**  
*Como estoquista, quero visualizar um destaque visual para produtos em situação crítica (estoque baixo ou vencimento próximo), para priorizar a reposição ou promoção desses itens.*  
**Status:** ✅ 

**História 13 — Criar dashboard com indicadores**  
*Como dono da loja, quero uma tela inicial com indicadores visuais sobre a saúde do negócio, para obter uma visão rápida dos principais dados do sistema.*  
**Status:** ✅ 

**História 14 — Exportação de dados para arquivo**  
*Como gerente, quero exportar a lista de produtos para um arquivo externo (ex: .csv), para realizar conferências físicas ou envio de relatórios.*  
**Status:** ✅  

<br>

**Histórias implementadas ao decorrer do andamento do projeto, para agregar mais valor:**

<br>

**História 15 — Controle de acesso por perfil**  
*Como administrador, quero restringir o acesso a funcionalidades sensíveis do sistema com base no cargo do usuário (Administrador vs. Funcionário), para garantir a segurança da informação e evitar alterações indevidas.*  
**Status:** ✅ 

**História 16 — Gestão de equipe e usuários**  
*Como administrador, quero uma tela de gestão para cadastrar, editar, excluir acessos e resetar senhas de funcionários, para manter o controle centralizado de quem acessa o sistema.*  
**Status:** ✅ 

**História 17 — Relatório de desempenho financeiro mensal**  
*Como dono do negócio, quero visualizar um relatório mensal contendo o faturamento bruto e lucro estimado, para acompanhar a saúde financeira e a rentabilidade da empresa.*  
**Status:** ✅

### Requisitos Não-Funcionais

**RNF-01 — Plataforma**  
O sistema deverá ser uma aplicação **desktop**, desenvolvida em **Java**, utilizando **JavaFX** para a interface gráfica.

**RNF-02 — Banco de dados**  
O sistema deverá utilizar **PostgreSQL** como sistema gerenciador de banco de dados, com conexão local via JDBC.

**RNF-03 — Usabilidade**  
A interface gráfica deverá ser **intuitiva e de fácil navegação**.

**RNF-04 — Desempenho**  
As operações de consulta, cadastro e atualização deverão responder em um tempo **inferior a 2 segundos** em ambiente local.

**RNF-05 — Segurança**  
O acesso ao sistema deverá ser protegido por autenticação de usuários, garantindo que apenas usuários autorizados possam acessar certas funcionalidades e informações.

**RNF-06 — Integridade dos dados**  
O sistema deverá garantir a integridade dos dados armazenados, evitando inconsistências em operações de entrada e saída de estoque.

**RNF-07 — Manutenibilidade**  
O código do sistema deverá ser organizado em camadas, facilitando manutenção, evolução e correções futuras.

**RNF-08 — Portabilidade**  
O sistema deverá ser compatível com os sistemas operacionais **Windows** e **Linux**.

---

## Gestão do Projeto

### Metodologia Utilizada

A metodologia utilizada no desenvolvimento do sistema foi o **Scrum**, conforme orientado na disciplina.  
A ferramenta Jira foi utilizada para organizar o backlog, as sprints e as tarefas do projeto.

### Papéis da Equipe

- **Product Owner (PO):** Alexander  
- **Desenvolvedor:** Alexander e Felipe

### Números do Projeto e Cerimônias
- **Data de kick-off:** 19/12/2025  
- **Total de sprints planejadas:** 3
- **Sprint Atual:** Projeto finalizado.  
    - **Período da Sprint 1:** 19/12/2025 a 06/02/2026
    - **Período da Sprint 2:** 07/02/2026 a 20/02/2026   
    - **Período da Sprint 3:** 21/02/2026 a 09/03/2026  

O projeto seguiu o cronograma de cerimônias do Scrum, com a realização de reuniões de Planning e Review. Abaixo, apresentamos a visão geral do nosso **Scrum Board** e do **Backlog** no Jira, evidenciando a organização das tarefas:

<p align="center">
  <img src="https://i.imgur.com/0hbvZmx.png" alt="Scrum Board no Jira"/>
  <br>
  <em>Figura 3 – Scrum Board do Projeto</em>
</p>

<p align="center">
  <img src="https://i.imgur.com/dEDuUzW.png" alt="Backlog no Jira"/>
  <br>
  <em>Figura 4 – Backlog no Jira</em>
</p>

Acompanhamos também o ritmo de entrega da equipe através do gráfico **Burndown**, que nos auxiliou a visualizar a queima de tarefas ao longo das sprints:

<p align="center">
  <img src="https://i.imgur.com/ZcluKyl.png" alt="Gráfico de Burndown"/>
  <br>
  <em>Figura 5 – Gráfico Burndown</em>
</p>

### Transbordo de Tarefas e Evolução do Backlog

Ocorreram pequenos transbordos de funcionalidades visuais da Sprint 2 para a Sprint 3, mas que foram rapidamente absorvidos. Novas histórias (como Gestão de Equipe e Relatórios Financeiros) foram descobertas e adicionadas ao escopo na reta final para agregar mais valor ao produto.

### Backlog Inicial e Backlog Atual

O backlog inicial foi composto pelas principais histórias do sistema, relacionadas a:
- Configuração do banco de dados;
- Telas de cadastro e listagem;
- Movimentação de estoque;
- Dashboard e relatórios.

No backlog atual, houve alterações: necessidades surgiram durante o desenvolvimento e viraram novas histórias (como o controle de permissões de usuários e a separação de faturamento financeiro).
O sistema atualmente está 100% finalizado e funcional, com todas as histórias entregues, incluindo o dashboard completo, tela de vendas, tela de cadastro de fornecedores e produtos, alteração no estoque, painel de relatórios exportáveis e controle de níveis de acesso.

---

## Análise e Projeto do Software

### Projeto Arquitetural

A arquitetura do sistema GEST segue o modelo de **arquitetura em camadas**, com separação entre interface gráfica, lógica de negócio e acesso a dados.

A estrutura geral do sistema é composta pelas seguintes camadas:

- **Camada de Apresentação (View):** responsável pela interface gráfica desenvolvida em JavaFX.
- **Camada de Controle (Controller):** responsável por receber as ações do usuário e coordenar as operações do sistema.
- **Camada de Serviço (Service):** responsável pelas regras de negócio.
- **Camada de Acesso a Dados (DAO):** responsável pela comunicação com o banco de dados PostgreSQL via JDBC.
- **Camada de Persistência:** banco de dados PostgreSQL.

Fluxo simplificado da arquitetura:

<p align="center">
  <img src="https://i.imgur.com/4lMjtEw.png"/>
  <br>
  <em>Figura 3 – Fluxo simplificado da arquitetura</em>
</p>

### Projeto de Componentes

O sistema foi organizado em pacotes (módulos) conforme sua responsabilidade, visando manter o baixo acoplamento e a alta coesão:

- **view:** telas JavaFX do sistema.
- **controller:** classes responsáveis por tratar eventos da interface.
- **model / core:** classes que representam as entidades do domínio do sistema (Produto, Fornecedor, Usuario, etc).
- **service:** regras de negócio e validações.
- **dao:** classes responsáveis pelo acesso direto ao banco de dados e persistência.
- **util/config:** classes auxiliares, como conexão com o banco e utilitários de segurança.

Abaixo, apresentamos o diagrama UML de pacotes/componentes que ilustra a dependência e a comunicação entre esses módulos na arquitetura do GEST:

<p align="center">
  <img src="URL_OU_CAMINHO_DO_SEU_DIAGRAMA_UML" alt="Diagrama UML de Componentes/Pacotes"/>
  <br>
  <em>Figura 6 – Diagrama UML de Componentes do Sistema</em>
</p>

### Propriedades e Princípios de Projeto

O projeto do GEST foi fundamentado em sólidos princípios de Orientação a Objetos, garantindo manutenibilidade, escalabilidade e baixo acoplamento entre os componentes. Abaixo estão os principais conceitos aplicados, com exemplos retirados da base real de código:

**1. Abstração e Herança**

Para evitar duplicação de código e modelar o domínio corretamente, a classe `Produto` foi definida como abstrata. Ela encapsula os atributos e métodos universais, permitindo que subclasses específicas herdem e estendam esse comportamento de acordo com suas regras de negócio.

*Trecho do sistema (core/Produto.java e core/ProdutoPerecivel.java):*

```java
public abstract class Produto implements IRelatorio {
    protected String descricao;
    protected String categoria;
    protected int codigo;
    // ... construtores e atributos encapsulados

    public abstract void exibirDetalhes(); // Obriga as filhas a implementarem
}
```
```java
public class ProdutoPerecivel extends Produto {
    private Date dataValidade;

    public ProdutoPerecivel(int codigo, String descricao, String categoria, int qntdDisp, double valorUnitVenda, double percentualLucro, Fornecedor fornecedor, String fabricante, Date dataValidade) {
        super(codigo, descricao, categoria, qntdDisp, valorUnitVenda, percentualLucro, fornecedor, fabricante);
        this.dataValidade = dataValidade;
    }
}
```

 
**2.Separação de Responsabilidades (Padrão DAO e MVC)**

A regra de negócio e o acesso a dados estão estritamente isolados da interface gráfica. A classe MainFX (View) não realiza nenhuma instrução SQL; ela interage com a camada DAO, que cuida exclusivamente da persistência usando JDBC.

**_Trecho do sistema (dao/ProdutoDAO.java):_**
```java
public class ProdutoDAO {
    // A classe encapsula a conexão JDBC e as queries SQL, isolando a complexidade
    public void salvar(Produto p) throws SQLException {
        String sql = "INSERT INTO produtos (codigo, descricao, categoria, quantidade, valor_venda, percentual_lucro, fornecedor_id, tipo_produto, data_validade, fabricante, meses_garantia) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // ... preparação do PreparedStatement e execução
    }
}
```

**3.Polimorfismo e Uso de Interfaces**

O sistema utiliza polimorfismo tanto na implementação de contratos quanto na instanciação de objetos. A interface IRelatorio define o comportamento de formatação, enquanto a camada DAO utiliza os recursos modernos do Java (Switch Expressions e Pattern Matching) para instanciar e manipular as subclasses dinamicamente.

_**Trecho do sistema (core/Produto.java e dao/ProdutoDAO.java):**_

```java
// Implementação do contrato da interface na classe abstrata
    @Override
    public void imprimirRelatorio() { gerarCabecalho(); gerarCorpo(); exibirDetalhes(); }

    // Retorno polimórfico no DAO instanciando a classe filha correta
    p = switch (tipo) {
        case "COSMETICO" -> {
            Date validadeCos = rs.getDate("data_validade");
            yield new Cosmetico(codigo, descricao, categoria, qtd, valor, lucro, f, validadeCos, fabricante);
        }
        case "ELETRONICO" -> {
            int garantia = rs.getInt("meses_garantia");
            yield new Eletronico(codigo, descricao, categoria, qtd, valor, lucro, f, fabricante, garantia);
        }
        case "PERECIVEL" -> {
            Date validadePer = rs.getDate("data_validade");
            yield new ProdutoPerecivel(codigo, descricao, categoria, qtd, valor, lucro, f, fabricante, validadePer);
        }
        default -> p;
    };
```


## DevOps e Automação (CI/CD)
 

Como diferencial do projeto, implementamos práticas de DevOps e Integração/Entrega Contínuas (CI/CD) utilizando o **GitHub Actions**.

Toda vez que um novo código é aprovado e enviado para a branch `main`, um *Workflow* automatizado (`gerar_exe.yml`) inicializa uma máquina virtual, compila o projeto e gera os executáveis nativos (o `.jar` universal e o `.exe` via Launch4j). Estes artefatos são anexados automaticamente à aba "Releases" do GitHub.

<p align="center">
  <img src="https://i.imgur.com/adsJ33e.png" alt="Pipeline de CI/CD no GitHub Actions"/>
  <br>
  <em>Figura V – Execução do fluxo automatizado de CI/CD</em>
</p>

---

## Conclusão

O projeto do GEST atingiu com sucesso a sua Versão Estável 1.0.0, cumprindo todos os requisitos propostos. Durante o ciclo da disciplina de Processo de Desenvolvimento de Software (PDS), o sistema passou por uma transformação arquitetural profunda: evoluiu de um simples projeto acadêmico estruturado em terminal para uma aplicação desktop completa, enriquecida por uma interface gráfica intuitiva (JavaFX), banco de dados em nuvem, controle de permissões por nível de acesso e geração de relatórios gerenciais exportáveis. 

As lições aprendidas ao longo do desenvolvimento foram imensuráveis, especialmente no que tange à aplicação prática de metodologias ágeis. A utilização da metodologia ágil Scrum foi fundamental para o sucesso das entregas, garantindo que o desenvolvimento fosse iterativo, focado no valor agregado ao cliente final e com margem de segurança para absorver o descobrimento de novas histórias nas etapas finais. Além disso, a aplicação rigorosa de padrões de projeto, como a separação em camadas (MVC e DAO), provou-se vital. Sem um código fracamente acoplado, a implementação de regras visuais complexas na reta final do projeto teria sido insustentável.

Apesar do sucesso na entrega final, o projeto apresentou desafios significativos ao longo das sprints. A principal dificuldade encontrada pela equipe foi a transição da interface via terminal (da versão anterior em POO) para a interface gráfica com JavaFX, o que exigiu uma curva de aprendizado acelerada sobre o ciclo de vida da UI, carregamento de arquivos FXML e a manipulação de eventos. Além disso, a configuração da conexão persistente com o banco de dados PostgreSQL e o correto alinhamento da esteira de automação no GitHub Actions demandaram diversas horas de pesquisa e testes até alcançarem a estabilidade necessária. Esses obstáculos, no entanto, foram fundamentais para o amadurecimento técnico do grupo e para a consolidação dos conhecimentos práticos de Engenharia de Software.
