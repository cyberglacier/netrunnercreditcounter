# Implementierung des Farbwahl-Menüs

Dieser Plan beschreibt die Hinzufügung eines Menüs, das sich öffnet, wenn man auf das Credit-Symbol in der Mitte klickt. Dieses Menü ermöglicht es, die Hintergrundfarbe der Runner- und Corp-Seite über horizontale Icon-Listen anzupassen.

## User Review Required

> [!IMPORTANT]
> Das bisherige Einstellungsmenü (Reset zu 5, Dark Mode) wird durch das neue Farbwahl-Menü ergänzt oder ersetzt. Ich schlage vor, die Reset-Funktion und den Theme-Wechsel ebenfalls in dieses neue Menü oder in einen Bereich darunter zu integrieren.

## Proposed Changes

### UI Layouts

#### [NEW] [dialog_color_selection.xml](file:///C:/Users/Christoph Lokal/AndroidStudioProjects/NetrunnerCreditCounter-Color-Expansion/app/src/main/res/layout/dialog_color_selection.xml)
Erstellt ein neues Layout für den Dialog:
- Eine Sektion für "Runner" mit horizontal angeordneten Faction-Icons (Anarch, Criminal, Shaper) und einer Option für Schwarz.
- Eine Sektion für "Corp" mit horizontal angeordneten Faction-Icons (HB, Jinteki, NBN, Weyland) und einer Option für Schwarz.
- Verwendung von `HorizontalScrollView`, um sicherzustellen, dass alle Icons auf kleineren Bildschirmen erreichbar sind.

### Logik

#### [MODIFY] [MainActivity.kt](file:///C:/Users/Christoph Lokal/AndroidStudioProjects/NetrunnerCreditCounter-Color-Expansion/app/src/main/java/com/example/netrunnercreditcounter/MainActivity.kt)
- Anpassung des `OnClickListener` für `binding.icon`.
- Implementierung der Logik zum Öffnen des `BottomSheetDialog` oder eines maßgeschneiderten `AlertDialog` mit dem neuen Layout.
- Zuweisung von Click-Listenern für jedes Icon im Dialog, um die Hintergrundfarbe von `binding.runnerSide` oder `binding.corpSide` zu ändern.
- Verknüpfung der Icons mit den in `colors.xml` definierten Farben.

## Verification Plan

### Manual Verification
1. App starten.
2. Auf das Credit-Symbol in der Mitte klicken.
3. Überprüfen, ob das Menü erscheint und die Icons horizontal angeordnet sind.
4. Auf ein Runner-Icon (z.B. Criminal) klicken und prüfen, ob sich der Hintergrund der unteren Hälfte blau färbt.
5. Auf ein Corp-Icon (z.B. Jinteki) klicken und prüfen, ob sich der Hintergrund der oberen Hälfte rot färbt.
6. Prüfen, ob die Reset-Funktion weiterhin zugänglich ist (falls integriert).
