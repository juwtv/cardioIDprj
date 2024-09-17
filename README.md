# Aplicação Android Auto para interação com o sistema CardioWheel

Este é um projeto de final curso da Licenciatura em Engenharia Informática e Multimédia, que visa integrar funcionalidades do Android Auto com o dispositivo *CardioWheel*, que recolhe e processa dados relacionados com informação cardíaca.


## Versões
Android Studio: 2023.3.1

## Estrutura do projeto

- **app/**: Contém a aplicação *CardioID BLE*.
- **cardioid_ble-release/**: Biblioteca fornecida pela *CardioID* para as funcionalidades BLE relacionadas com o *CardioWheel*.
- **segundatc/**: Contém a aplicação *CardioID AndroidAuto*.
- **shared/**: Contém recursos partilhados entre as aplicações.


## Repositório
Para correr o projeto deve ser feito um clone do [repositório](https://github.com/juwtv/cardioIDprj):

1. Clonar o repositório:
    ```bash
    git clone https://github.com/juwtv/cardioIDprj
    ```

2. Abrir o projeto no Android Studio.

3. Sincronizar o projeto com o Gradle.

4. Correr o módulo `app` para o dispositivo móvel.

5. Correr o módulo `segundatc` para o dispositivo móvel.

6. Correr o DHU (*Desktop Head Unit*, ou Unidade Central do Computador) para poder ver as funcionalidades Android Auto.

    
## Emulador
Para correr o emulador da Unidade Central do Computador , deve-se correr na linha de comandos, tendo em conta que se **deve adaptar o caminho** de acordo com a sua máquina:

**Conexão ao Desktop Head Unit:**
```
C:\Users\Hp\AppData\Local\Android\Sdk\platform-tools\adb forward tcp:5277 tcp:5277
```    

**Lançar ao Desktop Head Unit:**
```
C:\Users\Hp\AppData\Local\Android\Sdk\extras\google\auto\desktop-head-unit.exe
```

## Para testar:
Para testar as funcionalidades do *Android Auto* no DHU, ao criar um novo *Driver*: 
- O *username* deve ser **maior** do que 5 caracteres.
- A *password* inserida **tem de ser** obrigatoriamente: `password`.


## Características

O projeto "Aplicação Android Auto para interação com o sistema CardioWheel" inclui as seguintes características:

- **Integração Android Auto**: Suporta Android Auto, permitindo que os utilizadores visualizem dados cardíacos na condução.

- **Bluetooth Low Energy (BLE) Support**: Conexão e receção de dados de um dispositivo *CardioWheel* que processa dados cardíacos.

- **Modularidade**: Funcionalidade separada em módulos.

- **Comunicação entre aplicações**: Uma aplicação de comunicação *BLE* com o volante e outra que comunica com o *Android Auto*.


## Créditos:
Joana Pereira e Daniela Soares