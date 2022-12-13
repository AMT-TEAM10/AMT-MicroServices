# AMT-MicroServices

> Nicolas Crausaz & Maxime Scharwath

Ce laboratoire consiste à mettre en œuvre une application permettant le stockage `S3` et l'analyse d'image `Rekognition` sous forme de microservices. Les microservices 

# Prérequis

Pour utiliser cette application, il nécessaire d'avoir installé sur votre machine les dépendances suivantes :

- Java 18
- Maven 3.8

Voir [Wiki](https://github.com/AMT-TEAM10/AMT-Rekognition/wiki/D%C3%A9pendances) pour plus d'informations.

Vous devez également avoir à disposition : un Bucket AWS S3, ainsi que les clés d'accès publique et privée y permettant l'accès.

## Configuration

Ce repository est séparé en deux projets, un pour chaque microservice:

```
├── dataobject
├── labeldetector
```

Dans chacun de ces dossiers, il y a un fichier `.env.template` qui contient les variables d'environnement nécessaires pour l'exécution des microservices.

Copiez le contenu du fichier `.env.template` dans un nouveau fichier `.env` et ajoutez-y les informations nécessaires :

```
AWS_BUCKET_NAME= nom du bucket (ex: test-bucket.example.com)
AWS_ACCESS_KEY_ID= access_key (fournie par AWS)
AWS_SECRET_ACCESS_KEY= private_access_key (fournie par AWS)
AWS_REGION= region du bucket (ex: eu-west-2)
PUBLIC_LINK_VALIDITY_DURATION= durée de validité du lien public (en minutes)
```

## Utilisation

### Installation des dépendances

Les dépendances détaillées du projet se trouvent [ici](https://github.com/AMT-TEAM10/AMT-Rekognition/wiki/D%C3%A9pendances).

Pour installer les dépendances, entrer la commande suivante :

```bash
$ mvn install -DskipTests
```

### Exécution (en local)

Pour générer un exécutable, entrer la commande suivante:

```bash
$  mvn package
```

Pour exécuter le programme, copiez le fichier `./target/AMT-Rekognition-1.0-SNAPSHOT-jar-with-dependencies.jar`
`.env` et `main.jpeg` à l'endroit souhaité pour l'exécution, puis entrer la commande suivante :

```bash
$ java -jar AMT-Rekognition-1.0-SNAPSHOT-jar-with-dependencies.jar
```

> **Warning**
> Il est important d'avoir copié les autres fichiers au même niveau que l'exécutable

### Exécution (sur l'instance AWS)

Nous avons déjà déployé manuellement tous les fichiers nécessaires pour exécuter l'application sur le serveur de production.

Après s'être connecté sur l'instance, entrer les commandes suivantes:

```bash
$ cd app
$ docker compose up
```

L'application s'exécutera dans un container, et son résultat sera affiché en fin d'exécution.

Pour changer les credentials / variables d'env. sur l'environnement de production, il faut éditer les valeurs dans le fichier `app/docker-compose.yml`.

Des explications plus détaillées sont disponible dans le [Wiki](https://github.com/AMT-TEAM10/AMT-Rekognition/wiki/Configuration,-d%C3%A9ploiement-et-production)

# Tests

Après avoir installé les dépendances, il est possible d'exécuter les test en entrant la commande suivante :

```bash
$ mvn tests
```

ou alors, exécuter un test unique :

```bash
$ mvn test -Dtest="testName"
```

# Directives

Toute documentation relative aux practices, dépendances, architecture se trouvent dans notre [Wiki](https://github.com/AMT-TEAM10/AMT-Rekognition/wiki).