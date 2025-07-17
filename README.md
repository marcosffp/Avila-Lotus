# Ávila Lótus: Massoterapia

O projeto **Ávila Lótus** tem como objetivo desenvolver uma plataforma web personalizada para otimizar o agendamento e a gestão dos atendimentos da profissional de massoterapia **Valéria Ávila**. A proposta visa substituir os métodos manuais por um sistema digital automatizado, que ofereça maior controle sobre horários, informações de clientes, notificações por e-mail e gestão financeira, garantindo organização, segurança e praticidade tanto para a profissional quanto para seus clientes.

A plataforma permitirá que os clientes visualizem horários disponíveis, agendem sessões, preencham fichas de anamnese e enviem comprovantes de pagamento de forma simples e intuitiva, com atenção especial à acessibilidade para o público idoso. Já a profissional poderá administrar sua agenda, serviços oferecidos e relatórios financeiros com facilidade, elevando a qualidade do atendimento e reduzindo erros operacionais.

## Alunos integrantes da equipe

* [Bernardo Alvim](https://github.com/alvimdev)
* [Gabriela Alvarenga](https://github.com/gabialvarenga)
* [Marcos Alberto](https://github.com/marcosffp)
* [Mateus Araujo](https://github.com/teteuw)
* [Rafael Moura](https://github.com/RafaelMouraG)
* [Túlio Henrique](https://github.com/tulio44)

## Professores responsáveis

* Eveline Alonso
* Juliana Baroni

## Instruções de Utilização

Para executar o sistema Ávila Lótus localmente, é necessário configurar tanto o backend (Spring Boot) quanto o frontend (Astro), além de ter uma instância do MySQL rodando. Abaixo estão descritas as instruções para instalação das dependências e execução do sistema:

### Pré-requisitos

Certifique-se de ter os seguintes softwares instalados:

* [Node.js (v22 ou superior)](https://nodejs.org/)
* [Java 17+](https://adoptium.net/)
* [Maven](https://maven.apache.org/)
* [MySQL 8+](https://dev.mysql.com/downloads/mysql/)
* Git

---

### 1. Clonar o repositório

```bash
mkdir avila-lotus
git clone https://github.com/ICEI-PUC-Minas-PPLES-TI/plf-es-2025-1-ti3-8966100-avila-lotus.git ./avila-lotus
cd avila-lotus
```

---

### 2. Criar o arquivo `.env` no backend

Crie um arquivo chamado `.env` na raiz da pasta `Codigo/back` com o seguinte conteúdo:

```env
# Banco de Dados
DATABASE_HOST={endereço do servidor do banco de dados}
DATABASE_PORT={porta usada pelo MySQL}
DATABASE_NAME={nome do banco de dados}
DATABASE_USERNAME={usuário do banco}
DATABASE_PASSWORD={senha do banco}

# E-mails
MAIL_NO_REPLY={e-mail usado para envio de mensagens para redefinição de senha}
PASSWORD_NO_REPLY={senha do e-mail acima (senha de app do Gmail)}

MAIL_AGENDAMENTOS={e-mail usado para enviar notificações de agendamentos}
PASSWORD_AGENDAMENTOS={senha do e-mail acima (senha de app do Gmail)}

MAIL_CONTATO={e-mail usado para contato com a profissional}
PASSWORD_CONTATO={senha do e-mail acima (senha de app do Gmail)}

# Segurança
JWT_SECRET={chave secreta usada para geração de tokens de autenticação (JWT)}
COD_AUTENTICACAO={código numérico usado para validação cadastro da profissional}
```
---

### 3. Executar o backend (Spring Boot)

Navegue até o diretório do backend:

```bash
cd Codigo/back
```

Compile e execute a aplicação:

```bash
mvn spring-boot:run
```

O backend será iniciado na porta padrão `8080`:
[http://localhost:8080](http://localhost:8080)

---

### 4. Executar o frontend (Astro)

Abra outro terminal e vá até a pasta do frontend:

```bash
cd Codigo/Front
```

Instale as dependências:

```bash
npm install
```

Inicie o servidor de desenvolvimento:

```bash
npm run dev
```

O frontend estará disponível em:
[http://localhost:4321](http://localhost:4321)

---

### Observações

* Verifique se as chamadas feitas pelo frontend estão apontando corretamente para a API do backend (`http://localhost:8080` durante o desenvolvimento).
* Para o ambiente de produção, será necessário configurar corretamente as variáveis de ambiente e adaptar os caminhos de API no frontend.

---
