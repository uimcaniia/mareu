# Ma Réu

----


> Cette application permet aux collaborateurs de Lamzone de réserver des salles de réunions.
Elle est développé sur Android Studio sous Java 8 à compter de l'API 21..

----
## Fonctions principales
1. Afficher les différentes réunions.
2. Ajouter une réunion.
3. Supprimer une réunion.
4. Modifier une réunion.
5. Filtrer les réunions par date ou par salle.

----
## Installation

Récupérer le programme via le bouton Download ou en utilisant git clone via ce lien : [links](https://github.com/uimcaniia/mareu).

Lancer Android Studio, Open/File et choisir le fichier téléchargé. Compiler et exécuté en choisissant un émulateur réel ou virtuel. Cliquez sur Run/Run.

----
## Explications

A l'ouverture de l'application, les données sont vierges. Aucune réunion n'existe à l'avance.
Seules les listes des participants et des salles de réunions sont déjà conçues.

Dans la première activité, ajoutez une réunion avec le bouton "ajouter" en bas de l'écran d'accueil.
3 fragments s'affichent dans la deuxième activités.
1. Fragment 1 "Date et heure" : Choisissez la date et l'heure de la future réunion.
2. Fragment 2 "Participant" : Choisissez en cliquant sur les picto, les participants disponible de la future réunion.
3. Fragment 3 "Room", sujet et validation. Inscrivez le thème de la réunion, choisissez la salle de réunion dans la liste de salle disponible puis validez.

Un ou plusieurs messages d'erreurs apparaitront si la configuration de la réunion est impossible.

Cas où la validation est impossible :
1. La date ou l'heure sélectionnée est déjà passée.
2. La liste des participants dans le fragment 2, est une liste contenant que des participants disponible à la date et à l'heure sélectionnées dans le fragment 1. Une fois la sélections des participants faites dans le fragment 2, si vous changez ensuite la date dans le fragment 1 et que le nouvel horaire empiette sur une réunion déjà existante avec les même participants, un message vous indiquera que ces derniers ne sont pas disponible. Vous avez alors le choix entre changez l'horaire, ou prendre d'autres participants.
3. Même manipulation avec le choix de la salle de réunion. La liste qui s'affiche dans le fragment correspond aux salles disponible. Une fois la sélection faite, si vous changez également l'horaire, le même problème surviendra. la salle de réunion disparaitre alors de la liste, la valeur de votre salle de réunion sera à nulle. il faudra alors choisir entre une nouvelle salle de réunion, ou un autre horaire.

Une fois que plusieurs réunions ont été crées, vous pouvez filtrer leur affichage grâce au menu situé en haut à droite de la première activité.
1. Par date : Une boite de dialogue vous laisse choisir la date qui vous interesse et vous présentera a la suite, les réunions à cette date, trié par ordre chronologique dans la journée.
2. Par salle de réunion : Une boite de dialogue vous laisse choisir la salle qui vous interresse.
3. Retirer le filtre : Retourne la liste complète des réunions suivant leur date de création.

Si aucune réunion n'existe, le filtre ne peut être fait, une boite de dialogue vous indique que la demande ne peut être réalisé faute de réunion.

Vous pouvez supprimer une réunion en cliquant sur l'icone en forme de poubelle.

Pour modifier une réunion, cliquez sur l'item. Les données de la réunion seront chargées dans les 3 fragments de la deuxième activités.
Une fois modifié, pensez à valider pour sauvegarder.