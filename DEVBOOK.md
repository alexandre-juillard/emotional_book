# Présentation générale du projet

## Objectif du projet
Le projet EmotionalBook a pour objectif de créer un carnet de bord émotionnel permettant aux utilisateurs de suivre et d'analyser leurs émotions au fil du temps. Il vise à aider les utilisateurs à mieux comprendre leurs émotions, à identifier des schémas et à améliorer leur bien-être émotionnel.

## Contexte
Dans un monde de plus en plus stressant et complexe, il est essentiel de prendre soin de sa santé mentale et émotionnelle. Le suivi des émotions peut aider les individus à mieux comprendre leurs réactions émotionnelles, à identifier les déclencheurs de leurs émotions et à développer des stratégies pour les gérer efficacement. EmotionalBook s'inspire de la roue des émotions de Plutchik pour fournir un cadre structuré pour le suivi des émotions.
La volonté de ce projet est de fournir une application mobile accessible à tous, permettant de suivre ses émotions de manière simple et intuitive, tout en garantissant la sécurité et la confidentialité des données des utilisateurs. Le choix de l'application mobile permet aux utilisateurs d'enregistrer leurs émotions à tout moment et en tout lieu, facilitant ainsi l'intégration de cette pratique dans leur vie quotidienne.

## Public cible
Le public cible d'EmotionalBook comprend :
- Les individus souhaitant améliorer leur bien-être émotionnel et mental.
- Les personnes cherchant à mieux comprendre leurs émotions et à identifier des schémas dans leur vie émotionnelle.
- Les professionnels de la santé mentale souhaitant utiliser l'application comme outil de suivi pour leurs patients.
- Les étudiants et les jeunes adultes cherchant à développer des compétences en gestion des émotions.

## Fonctionnalités principales
1. **Suivi des émotions** : Les utilisateurs peuvent enregistrer leurs émotions quotidiennes, en sélectionnant parmi une liste issue de la roue des émotions de Plutchik. Puis ils ajoutent le ou les éléments déclencheurs de l'émotion, ainsi que des notes supplémentaires pour contextualiser leur ressenti. Et enfin la stratégie adoptée pour gérer cette émotion.
2. **Analyse des émotions** : Le carnet de bord permet aux utilisateurs de visualiser leurs émotions sur une période donnée, en générant des graphiques et des statistiques. Cela inclut la fréquence des émotions, les émotions les plus courantes, et les tendances au fil du temps.
3. **Rappels et notifications** : Les utilisateurs peuvent configurer des rappels pour les inciter à enregistrer leurs émotions régulièrement. Des notifications peuvent également être envoyées pour encourager les utilisateurs à réfléchir sur leurs émotions et à pratiquer des techniques de gestion des émotions.
4. **Journal de bord** : Les utilisateurs peuvent écrire des entrées de journal pour approfondir leurs réflexions et leurs expériences émotionnelles. Cela peut inclure des réflexions sur des événements spécifiques, des stratégies de gestion des émotions, ou des moments de gratitude.
5. ** Exportation des données** : Les utilisateurs peuvent exporter leurs données émotionnelles sous forme de fichier CSV ou PDF pour une analyse plus approfondie ou pour les partager avec un professionnel de la santé mentale.
6. **Sécurité et confidentialité** : Les données des utilisateurs sont stockées de manière sécurisée, avec des options de chiffrement et de sauvegarde. Les utilisateurs peuvent également choisir de rendre leurs données anonymes pour une utilisation dans des études ou des recherches.

## Technologies utilisées
- **API** : L'application utilise une API RESTful en Java pour gérer les opérations de suivi des émotions, d'analyse et de journal de bord.
- **Base de données** : Les données des utilisateurs sont stockées dans une base de données relationnelle (par exemple, MySQL ou MongoDB) pour assurer la persistance et la sécurité des données.
- **Interface mobille** : L'application dispose d'une interface mobile développée en Kotlin, permettant aux utilisateurs d'accéder à leurs données et de les enregistrer facilement depuis leur smartphone.
- **Sécurité** : Des protocoles de sécurité tels que HTTPS et OAuth2 sont utilisés pour protéger les données des utilisateurs et garantir la confidentialité des informations sensibles.

## Cahier des charges
Le cahier des charges du projet EmotionalBook comprend les spécifications fonctionnelles et techniques suivantes :
1. **L'authentification** : L'utilisateur doit pouvoir créer un compte, se connecter et se déconnecter de l'application. L'authentification doit être sécurisée et respecter les normes de sécurité actuelles. L'utilisateur doit pouvoir se connecter avec un compte Google ou Microsoft via OAuth2.
2. **Page de profil** : L'utilisateur doit pouvoir consulter et modifier son profil, y compris ses informations personnelles et ses préférences de notification.
3. **Page d'accueil** : L'utilisateur doit pouvoir accéder à un tableau de bord affichant un résumé de ses émotions récentes, des graphiques de suivi et des rappels pour enregistrer ses émotions. Un bouton permet d'ajouter une nouvelle entrée d'émotion.
4. **Page de suivi des émotions** : L'utilisateur doit pouvoir enregistrer une nouvelle émotion en sélectionnant une émotion de la roue de Plutchik, en ajoutant des déclencheurs, des notes et une stratégie de gestion. L'utilisateur doit également pouvoir consulter ses entrées passées et les modifier si nécessaire.
5. **Page d'analyse des émotions** : L'utilisateur doit pouvoir visualiser ses émotions sur une période donnée, avec des graphiques et des statistiques. Il doit pouvoir filtrer les données par date, type d'émotion et autres critères pertinents. Cette page est accessible depuis la page d'accueil, dans le tableau de bord.
6. **Page de journal de bord** : L'utilisateur doit pouvoir écrire des entrées de journal, les consulter et les modifier. Les entrées doivent être organisées par date et accessibles depuis la page d'accueil.
7. **Notifications et rappels** : L'utilisateur doit pouvoir configurer des rappels pour enregistrer ses émotions et recevoir des notifications pour les inciter à réfléchir sur leurs émotions. Les notifications doivent être personnalisables en termes de fréquence et de contenu.
8. **Exportation des données** : L'utilisateur doit pouvoir exporter ses données émotionnelles sous forme de fichier CSV ou PDF. L'exportation doit inclure les entrées d'émotions, les déclencheurs, les notes et les stratégies de gestion. L'exportation se fera par l'envoie d'un mail à l'utilisateur avec les données en pièce jointe.
9. **Sécurité et confidentialité** : Les données des utilisateurs doivent être stockées de manière sécurisée, avec des options de chiffrement et de sauvegarde. L'application doit respecter les normes de sécurité actuelles et garantir la confidentialité des informations sensibles. Les utilisateurs doivent pouvoir rendre leurs données anonymes pour une utilisation dans des études ou des recherches.

## Plan de développement
Le développement du projet EmotionalBook sera réalisé en plusieurs étapes :
1. **Phase de conception** : Élaboration des maquettes de l'interface utilisateur
2. **Phase de conception de la base de données** : Conception du schéma de la base de données pour stocker les émotions, les déclencheurs, les notes et les stratégies de gestion.
3. **Phase de développement de l'API** : Création de l'API RESTful en Java pour gérer les opérations de suivi des émotions, d'analyse et de journal de bord.
4. **Phase de développement de l'interface mobile** : Développement de l'application mobile en Kotlin, intégration de l'API et mise en place des fonctionnalités principales.
5. **Phase de tests** : Tests unitaires et d'intégration pour garantir le bon fonctionnement de l'application et la sécurité des données.
6. **Phase de déploiement** : Déploiement de l'application sur les plateformes Android et iOS, avec une attention particulière à la sécurité et à la confidentialité des données des utilisateurs.

