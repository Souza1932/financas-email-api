## PASSO A PASSO DE INSTALAÇÃO DO SERVIDOR

1. Instale o Java 21 e o Maven, necessários para compilar e rodar o backend. 
Comando de instalação: sudo apt update sudo apt install openjdk-21-jdk maven. 
2. Baixe o MySQL no terminal Linux-Ubuntu  https://www.youtube.com/watch?v=Uuw4KPiVATc 
3. Acesse o console do MySQL com o comando de acesso: sudo mysql -u root -p
4. Digite a senha do MySQL
5. Insira o comando CREATE DATABASE financas 
6. Insira o comando USE financas 
7. COMANDO MYSQL PARA CRIAR USUÁRIO DA APLICAÇÃO: CREATE USER IF NOT EXISTS 'financas_app'@'localhost' IDENTIFIED BY 'SuaSenhaForte123'; 
   No lugar de SuaSenhaForte123 CRIE UMA SENHA PARA SUBSTITUIR. 
8. DAR PERMISSÃO AO USUÁRIO: GRANT ALL PRIVILEGES ON financas.* TO 'financas_app'@'localhost'; 
9. APLICAR AS PERMISSÕES: FLUSH PRIVILEGES;
10. Saia do console do MySQL com o comando: EXIT; 

11. Extrair o arquivo financas-email-api 
12. Acesse o arquivo financas-email-api 
13. Clique no arquivo src 
14. Clique no arquivo main 
15. Clique no resources 
16. Clique no application.properties

## NO APPLICATION.PROPERTIES

17. Digite sua senha neste trecho spring.datasource.password= SUA_SENHA_FINANCAS_APP                  APAGUE O TRECHO SUA_SENHA_FINANCAS_APP 
18. Digite seu e-mail neste trecho spring.mail.username=DIGITE_SEU_E-MAIL                             APAGUE O TRECHO DIGITE_SEU_E-MAIL 
19. No trecho: spring.mail.password= DIGITE_SENHA_GOOGLE
    LINK para criar a senha myaccount.google.com/apppasswords 
20. Nome do app ou App name 
21. Cole a senha neste trecho spring.mail.password=DIGITE_SENHA_GOOGLE                                APAGUE DIGITE_SENHA_GOOGLE
22. Salva as informações atualizadas no arquivo.

## NO TERMINAL LINUX UBUNTU

23. Abra o terminal do Linux-Ubuntu 
24. Insira o comando cd ~/financas-email-api 
25. Insira o comando:  mvn spring-boot:run

COMANDO MYSQL EXETRAS CASO PRECISEM

TROCAR SENHA: ALTER USER 'financas_app'@'localhost' IDENTIFIED BY 'SUA_SENHA_NOVA_AQUI'; 
SELECT*FROM usuarios; 
VERIFICAR A SENHA DE PRIVILÉGIUS: mysql -u financas_app -p -h 127.0.0.1 financas -e "SELECT 1;"
