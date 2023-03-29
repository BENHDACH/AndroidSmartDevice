Auteurs : Mohamed BENHDACH
Date de la dernière version : 29/03/2023
Github : https://github.com/BENHDACH/AndroidSmartDevice

<\Ce projet consiste en une application pour établir une connection BLE vers un appareil.> 

Note : Le readme à pour usage d'expliquer à l'utilisateur l'application et non d'expliquer le code, pour cela des commentaires sont disponnible dans le code.

--------------------Page d'accueil <\MainActivity>
Cette page contient :
* Le titre du projet
* Le nom et prénom du créateur, ainsi que son école, classe et spécialité.
* Une description des fonctionnalités et objectif marqué par un check (ou une croix si non atteint)
* Un bouton « SCAN ! » vous dirigeant vers la page de scan.


--------------------Page de scan <\ScanActivity>
Cette page contient :
* Une demande de permissions si non accordées préalablement/précédemment.
* Une légende aidant à comprendre la valeur rssi.
* Une image Home permettant de retourné vers la page d'accueil lors du click sur cette dernière.
* Un bouton « LANCER LE SCAN » , permettant de démarrez le scan et afficher les appareils détecter (dans un recycler view).

=> Pendant scan : 
* Le bouton « LANCER LE SCAN » devient « ARRETER LE SCAN » et permet de stopper l'opération de scan.
* Une progress bar et un message "Scan en cours..." s'affichent le temps du scan. 

=> Après scan :
* Au clic sur un des appareils détecter, vous passerez à la dernière page dite de connexion.


 --------------------Page de connexion <\DeviceActivity>
Cette page contient :
* L'adresse MAC de l'appareil choisie
* Un chargement qui s'effectue le temps que la connection soit établie 
* Une image Home permettant de retourné vers la page d'accueil lors du click sur cette dernière.

=> Si echec de la connexion :
* Un messsage d'erreur et un icone bluetooth avec une croix apparaisent.

=> Si connexion établie : 
* 3 image de LED et un message indiquant le nombre d'incrémentation apparaisent
* Au clic sur une des LED, l'image devient jaune et le nombre d'incrémentation augmente de 1. Il est possible d'éteindre et allumer les   
  image LED visuellement.


 --------------- Merci !!!




