# AMT-MicroServices

> Nicolas Crausaz & Maxime Scharwath

Ce laboratoire consiste à mettre en œuvre une application permettant le stockage `S3` et l'analyse d'image `Rekognition` sous forme de microservices. Les microservices 

# Prérequis

Pour utiliser cette application, il nécessaire d'avoir installé sur votre machine les dépendances suivantes :

- Java 18
- Maven 3.8

Voir [Wiki](https://github.com/AMT-TEAM10/AMT-Rekognition/wiki/D%C3%A9pendances) pour plus d'informations.

Vous devez également avoir à disposition : un Bucket AWS S3, ainsi que les clés d'accès publiques et privées y permettant l'accès.

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
$ cd <dataobject | labeldetector>
$ mvn install -DskipTests
```

### Exécution (en local)

Pour démarrer l'application web en local, exécutez la commande suivante :
```bash
$ mvn spring-boot:run
```

> **Warning**
> Il est important d'avoir placé et rempli le fichier .env à ce moment-là.

### Exécution (sur l'instance AWS)

Nous avons déjà déployé manuellement tous les fichiers nécessaires pour exécuter l'application sur le serveur de production.

Après s'être connecté sur l'instance, entrer les commandes suivantes :

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

# API des microservices

La documentation d'utilisation des API des deux microservices se trouvent dans le readme de chaque projet respectif.

DataObject: [Documentation API](./dataobject/README.md)
LabelDetector: [Documentation API](./labeldetector/README.md)

# Docker

Nous avons créé une image Docker pour chacun des microservices:

[Dockerfile DataObject](./dataobject/Dockerfile)
[Dockerfile LabelRekognition](./labeldetector/Dockerfile)

Les deux images sont automatiquement publiées dans le registry DockerHub lors d'un succès de la pipeline CI/CD,
si les tests passent. Les images sont disponibles [ici](https://hub.docker.com/u/nicrausaz)

Nous avons également créé un Docker-Compose qui permet de démarrer rapidement les deux micro-services afin de les tester
avec le "main". Pour l'exécuter il faut éditer le fichier [docker-compose.yml](./main/docker/docker-compose.yml) pour y 
spécifier les secrets nécessaires (remplacer les valeurs `${{ secrets.ABC }}`), puis il suffit de lancer la commande:
```bash
$ cd main/docker
$ docker-compose up
```
