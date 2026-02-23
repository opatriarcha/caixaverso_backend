# Bank Hibernate Repositories (Hibernate ORM 7.2.5.Final)

Exemplo **sem Spring** usando Hibernate + H2 (em memória) com relacionamentos:
- 1:1 (Customer <-> Address, shared PK com @MapsId)
- 1:N (Customer -> Account)
- N:1 (Account -> Customer)
- N:N (Branch <-> Employee)

## Rodar testes
```bash
mvn test
```
