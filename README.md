# cloud-storage
Netty client-server application

Клиент:
  Client - запуск основного приложения
  hello-view.fxml - форма главного окна
  newFolderView.fxml - вспомогательная форма для создания новой папки
  ClientNetwork.java - инициализация Netty для клиента
  ClientInboundHandler.java - handler для входящих сообщений для клиента
  MainController.java - основной контроллер для клиента
  ServerController.java HostController.java - контроллеры для взаимодействия с окнами хоста и сервера
Сервер:
  Server - запуск сервера
  NettyBaseServer.java - инициализация Netty для сервера
  ServerHandler.java - обработчик входящих сообщений
Exchange: 
  MessageExchange.java - интерфейс определяющий тип для передачи сообщений между сервером и клиентом
  Action.java PartFile.java FileName.java FileList.java - реализация интерфейса MessageExchange отвечает за различные типы сообщений

Функционал:
  - Доделать навигацию между папками на клиенте и сервере
    - кнопка "Назад" для клиента ✅
    - кнопки "Наазад" для сервера ✅
- Передача файлов от клиента -> серверу
    - Реализовать функцию перадачи файла по нажатию кнопки "Загрузить" ✅
    - Удаление ✅
    - Копирование ✅
    - Создание новой папки ✅
    - Статус подключения сверху ✅
    - Окно сообщений/логов ✅
