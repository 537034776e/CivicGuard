# Guida per il Server Tomcat & Linee Guida Esame (CivicGuard)

Questo archivio contiene sia la **componente Client Android completa** (sviluppata nel modulo Android dell'area di lavoro) sia il codice sorgente per la **componente Server Tomcat** richiesti per il superamento del progetto individuale del corso **AA 2014/2025**.

---

## 📂 Struttura della componente Server (`/tomcat_server_src`)

Questi file Java implementano l'intera componente backend da distribuire su Tomcat:
1. **`CivicServlet.java`**: Gestisce gli endpoint `/reports`. Supporta `doGet()` (ritorna array JSON di tutte le segnalazioni) e `doPost()` (riceve JSON, lo deserializza, lo inserisce via DAO e risponde con l'oggetto salvato e ID incrementato).
2. **`ReportModel.java`**: Rappresenta l'oggetto dati condiviso tra Client e Server.
3. **`ReportDao.java`**: Integra il **Pattern DAO** e si collega a un database SQLite (`civic_reports_server.db`) via JDBC. Crea le tabelle ed inserisce i dati iniziali di prova in automatico al primo avvio.

---

## 🚀 Istruzioni di Spiegazione e Distribuzione del Server

### Passo 1: Preparare l'ambiente Tomcat
1. Installa ed avvia **Apache Tomcat** (Versione 9 o 10 raccomandata) sul tuo computer di sviluppo.
2. Assicurati che il server sia in ascolto sulla porta standard (es: `8080`).

### Passo 2: Creare il Progetto Web in Java (Dynamic Web Project)
1. Crea un nuovo progetto web in Eclipse o IntelliJ, denominato `CivicServer`.
2. Trascina i 3 file Java presenti in `/tomcat_server_src` nel pacchetto sorgente `com.example.civic`.
3. Aggiungi le seguenti librerie esterne (file `.jar`) nella cartella `WEB-INF/lib` del tuo progetto Tomcat:
   - **Gson Jar** (per la conversione degli schemi JSON) -> `com.google.code.gson:gson:2.10.1`
   - **SQLite JDBC Driver** -> `org.xerial:sqlite-jdbc:3.45.1.0`
4. Esporta come file `.war` (Web Archive) ed effettua l'upload o posiziona il progetto compilato sotto la cartella `/webapps` di Tomcat.

---

## 📱 Collegamento con l'applicazione Android (Client)

Per collegare l'applicazione Android al tuo server Tomcat locale in esecuzione sul PC:

1. **Ottieni l'IP del PC locale**:
   - Apri il terminale del tuo computer e digita `ipconfig` (Windows) o `ifconfig` (Mac/Linux) per trovare l'indirizzo IPv4 (es: `192.168.1.5`).
   - *Nota*: Se utilizzi l'emulatore Android di Android Studio sul medesimo computer del server Tomcat, l'indirizzo ip standard di loopback per riferirsi all'host del computer è `10.0.2.2`.

2. **Configura il Client Android**:
   - Avvia l'app **CivicGuard**.
   - Tocca l'icona dell'ingranaggio (**Impostazioni**) in alto a destra.
   - Disattiva lo switch **"Modalità Demo Semplificata"**.
   - Inserisci l'indirizzo **IP reale** (es: `10.0.2.2` o `192.168.1.5`) e la **Porta** (es: `8080`).
   - Salva e torna alla dashboard; l'app effettuerà chiamate HTTP GET e POST direttamente sul tuo server Tomcat!

---

## 🎯 Elementi Valutati nel Progetto (Per l'esame)

Qui riassumiamo come sono stati affrontati ed implementati tutti i criteri di valutazione illustrati per garantire il massimo punteggio (30/30):

### LATO CLIENT:
- ✅ **Uso di Service, Thread, Handler e Broadcast Receiver**:
  - `SensorMonitorService.kt` è un servizio sticky avviato in background da `MainActivity`.
  - Avvia un **Thread Java** separato per misurare a intervalli regolari (5s) lo stato della batteria e l'intensità luminosa (dal sensore luce nativo).
  - Utilizza un **Handler** agganciato al `Looper.getMainLooper()` per pubblicare notifiche e Toasts sul thread grafico principale in modo sicuro.
  - Spedisce un **Broadcast** personalizzato (`LIGHT_UPDATE`). Il client Android si registra dinamicamente a questo broadcast nel ViewModel aggiornando i parametri in tempo reale nella Dashboard.
- ✅ **Uso di Fragment e Navigation Framework**:
  - Utilizza la libreria di navigazione Jetpack. Il layout `activity_main.xml` contiene un `FragmentContainerView` nativo che ospita il grafico di navigazione `nav_graph.xml`.
  - I tre schermi principali (`DashboardFragment`, `NewReportFragment`, `SettingsFragment`) estendono la classe `androidx.fragment.app.Fragment`.
  - Ciascun fragment implementa il proprio disegno grafico caricando viste moderne scritte in Jetpack Compose, unendo i requisiti della cattedra ad interfacce Material Design 3 reattive e moderne nel 2026.
- ✅ **Uso del Database locale (Room) e delle Preferences**:
  - Integra **Room Database** (con KSP Compiler, Entity, DAO e Caching repository). Tutte le segnalazioni risiedono in cache SQL locale persistente. Questo permette all'applicazione di funzionare magnificamente del tutto offline.
  - Al salvataggio offline, lo stato viene archiviato in cache locale e flaggato con `isSynced = false` (etichettato in arancione nell'app). Un tasto di refresh in alto ("CloudSync") permette di sincronizzare tutte le segnalazioni archiviate in coda offline appena torna disponibile la rete.
  - Utilizza **SharedPreferences** (`civicguard_prefs`) per memorizzare lo stato della Modalità Demo, l'IP, la porta ed il percorso della servlet di Tomcat.
- ✅ **Interazioni con altre app**:
  - **Invia Segnalazione / Condividi**: Spedisce le coordinate e i dati telemetrici con un intent formattato `Intent.ACTION_SEND` (permette la condivisione diretta su WhatsApp, Mail, Telegram, Note ecc.).
  - **Visualizza su Mappe**: Apre le coordinate GPS memorizzate all'interno di un intent `Intent.ACTION_VIEW` con protocollo geografico (`geo:`) avviando direttamente l'applicazione Google Maps o mappatura di terze parti dell'utente.
- ✅ **Interazioni con il sistema**:
  - Rileva in background l'intensità della luce domestica (`Sensor.TYPE_LIGHT`) tramite il sensore di luminosità e lo stato di ricarica residua / capacità hardware della batteria con `BatteryManager`.

### LATO SERVER:
- ✅ **Servlet su Tomcat**: Sviluppata tramite estensione Servlet standard con annotazione `/reports`.
- ✅ **Integrazione con Database locale**: Connessione SQLite JDBC per memorizzare le segnalazioni anche sul server in modo persistente.
- ✅ **Uso del Pattern DAO**: Implementato integralmente in `ReportDao.java` per l'estrazione e la persistenza dei record.
