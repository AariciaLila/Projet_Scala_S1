# Projet_Scala_S1
Le but du projet de semestre est de construire un algorithme de dessin, permettant de créer diverses formes géométriques dans la console.

Le programme marche de la façon suivante :

1. La canvas (i.e. la toile de dessin) est affichée 
2. Le programme demande l'action à exécuter
3. L'utilisateur écrit une commande composée d'une action et éventuellement d'arguments (exemple : "draw_line 0,1 2,5 x") - Ici l'action est draw_line, et les arguments sont 0,1 - 2,5 - x
4. Le programme applique l'action sur la canvas et retourne à l'étape 1.

La base du programme est fournie dans le fichier Main.scala (permettant de gérer la boucle d'excéution, la lecture de l'input utilisateur, ..., notre rôle a consisté à implémenter les différentes actions.

Pour lancer le programme :
- Compiler le fichier : scalac Main.scala
- L'exécuter : scala Main

Une fois les diverses actions implémentée, en les appelant successivement il sera possible de dessiner dans la canvas.
