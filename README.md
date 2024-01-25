# gretl-job-starter

## Beschreibung

Quellcode des GRETL-Job-Start-Services. Der Service nimmt einen GET-Request entgegen (Jobname und Credentials) und startet mittels POST-Request den gewünschten GRETL-Job in Jenkins. Credentials bestehen aus Loginname und Token. Das Token muss `Configure` - `API-Token` erstellt werden. D.h. es gibt verschiedene Tokens pro GRETL-Jenkins-Umgebung (Test, Integration, Produktion). Der Service liefert ein Redirect auf die Job-Übersicht zurück (302).

Zum jetzigen Zeitpunkt wird nur die Produktionsumgebung unterstützt. Das `application.properties` ist dahingehend vorbereitet, dass es mehrere Umgebungen unterstützt. Im Controller wird jedoch immer nur die "Prod"-URL ausgelesen.

Verwendung in z.B. QGIS: In QGIS muss eine Action definiert werden. Das Token muss in einer Umgebungsvariable gespeichert: `Settings` - `System` - `Environment`. Als Variablen(namen) wählt man z.B. `gretl_token` und als Value den von Jenkins erzeugen Token. Achtung: QGIS muss neu gestartet werden. Der Benutzernamen ist bereits als Variable vorhanden. Wenn QGIS auf einem lokalen Gerät (ohne Single Sign On o.ä.) verwendet wird, muss man jedoch eine Variable mit dem Windows-Passwort definieren. Die Action muss vom Typ `Open URL` sein. Im Action Text Feld muss die zu öffnende URL stehen **FIXME**:

```
http://localhost:8080/start?job=agi_dummy&user=[% @user_account_name %]&token=[% @gretl_token %]
```

Im obigen Beispiel wird der Job "agi_dummy" ausgeführt.

## Komponenten

Es handelt sich um die einzige Komponente. 

## Konfigurieren und Starten

Die Anwendung kann am einfachsten mittels Env-Variablen gesteuert werden. Es stehen aber auch die normalen Spring Boot Konfigurationsmöglichkeiten zur Verfügung (siehe "Externalized Configuration").

| Name | Beschreibung | Standard |
|-----|-----|-----|
| `LOG_LEVEL_SPRING` | Loglevel des Spring Frameworks. | `INFO` |
| `LOG_LEVEL_APP` | Loglevel der eigenen Klassen. | `DEBUG` |

### Java

Benötigt Java 21:

```
java -jar build/libs/jobstarter-0.0.LOCALBUILD.jar
```

### Native Image


```
./build/native/nativeCompile/jobstarter
```

### Docker

Es gibt nur ein Dockerimage (AMD64) mit dem Native Image:

```
docker run -p8080:8080 sogis/gretl-job-starter:latest
```

## Externe Abhängigkeiten

Keine.

## Konfiguration und Betrieb in der GDI

Siehe sogis/openshift-templates Repo.

## Interne Struktur

Ein Controller nimmt den GET-Request entgegen, wandelt in in einen POST-Request um und liefert diesen an eine GRETL-Jenkins-Instanz aus. Falls der Job gestartet werden konnte (Statuscode 201), wird ein Redirect an den Client zurückgeliefert. Bei allen anderen Statuscodes wird eine Exception geworfen.

## Entwicklung

Entwicklung gegen `agi_dummy`-Job in Test und Produktion.

### Build

#### JVM
```
./gradlew clean build
```

#### Native

Benötigt GraalVM 21:

```
./gradlew nativeBuild
```

**FIXME:** native tests?
