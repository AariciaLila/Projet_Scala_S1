# Projet Scala Groupe 3

Ce programme Scala vous permet d'exécuter des commandes pour dessiner et modifier une canvas dans une console.

## Instructions pour lancer le programme

Le programme doit être démarré en ouvrant le fichier avec un éditeur de code tel que VSCode et en y installant l'extension Scala. Exécutez ensuite le programme avec le terminal.

Pour lancer le programme :
- Compiler le fichier : scalac Main.scala
- L'exécuter : scala Main

## Liste des différentes actions implémentées

- exit : quitte le programme
- dummy : créer une canvas pré-définie de taille 3 x 4 ; aucun argument est attendu
- dummy2 : créer une canvas pré-définie de taille 3 x 1 ; aucun argument est attendu
- new_canvas : créer une canvas à partir de la largeur et de la heuteur données et d'un caractère par défaut pour chaque pixel ; 3 arguments sont attendus: width (la largeur de la canvas, ex: 5), height (la hauteur de la canvas, ex: 3), character (le character par défaut de chaque pixel, ex: .)
- load_image : créer une canvas à partir d'un fichier existant ; 1 argument est attendu: filename (le nom du fichier à lire, ex: triforce)
- update_pixel : met à jour un pixel du canvas à partir de ses coordonnées, en changeant sa couleur ; 2 arguments sont attendus: pixel (les coordonnées du pixel à modifier, au format "x,y", ex: 8,3), color (le nouveau caractère, i.e., la couleur, du pixel, ex: X)
- draw_line : dessine une ligne sur la canvas ; 3 arguments sont attendu: pixel1 (les coordonnées du pixel de départ, au format "x,y", ex: 4,3), pixel2 (les coordonnées du pixel d'arrivée, au format "x,y", ex: 14,3), color (la couleur de la ligne, i.e. de tous les pixels entre celui de départ et d'arrivée, ex: X)
- draw_rectange : dessine un rectangle sur le canvas ; 3 arguments sont attendu: pixel1 (les coordonnées d'un angle du rectangle, au format "x,y", ex: 3,1), pixel2 (les coordonnées de l'angle opposé au format "x,y", ex: 15,4), color (la couleur de contour du rectangle, ex: X)
- draw_fill : remplit une forme sur la canvas ; 2 arguments sont attendu: pixel (pixel de départ à partir duquel colorier, au format "x,y", ex: 4,2), new_color (nouvelle couleur à appliquer, ex: *)

## Limites connues 

- La canvas ne peut pas être agrandi ou réduite une fois qu'elle a été créé, il faut la recréer pour cela.
- Les dessins sur la canvas ne peuvent être que des lignes droites et des rectangles verticaux ou horizontaux.

## Explication des différents choix techniques

- L'architecture Modèle-Vue-Contrôleur (MVC) a été utilisée pour structurer le programme, avec les classes Pixel et Canvas formant le modèle, la classe Main jouant le rôle de contrôleur et la sortie console servant de vue. Cette architecture rend le code plus facile à comprendre et à modifier.
- Pour simplifier le code et faciliter l'utilisation de pattern matching, il a été choisi d'utiliser des classes case pour Pixel et Canvas.
- Pour stocker les pixels, la classe Canvas utilise une matrice bidimensionnelle qui est implémentée à l'aide d'un Vector. Cette structure de données offre une taille dynamique ainsi qu'un accès en temps constant.
- Les fonctions d'actions sont séparées de l'implémentation de la classe Canvas et sont stockées dans le compagnon object de cette dernière. Cette approche permet de clarifier la logique du programme et de simplifier le code.

## Autres informations

À ce jour, aucun bug n'a été découvert dans le programme. Toutefois, il existe des limites à son fonctionnement, notamment l'incapacité à gérer des dessins complexes.

## Partage des tâches

Le code a été écrit par trois développeurs différents qui se sont dispersées les tâches :

- Aaricia : création du GitHub, mise en place du code de base, implémentation de l'action new_canvas, implémentation de l'action draw_line, ajout de la gestion des erreurs pour chaque action du programme, ajout des commentaires dans le code final, mise en place du fichier readme.md final
- Thomas : modification de la méthode display pour afficher la canvas dans la console, implémentation de l'action load_image, implémentation de l'action draw_rectangle
- Aboubacar : implémentation des méthodes apply du companion object Pixel, implémentation de l'action update_pixel, implémentation de l'action draw_fill
