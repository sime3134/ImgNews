# ImgNews

## Instruktioner
1. Börja med att ladda ner eller klona projektet här på GitHub.
2. Vi behöver nu lägga in aktuella API-nycklar. Skapa en textfil i huvudmappen som du döper till api_keys.txt.
3. Be om nycklar från utvecklarna eller skapa egna nycklar på:
   - https://newsapi.org/
   - https://beta.openai.com
4. Fyll i nycklarna enligt följande:
   - openai="nyckel"
   - newsapi="nyckel"
5. Högerklicka på klassen “Launcher” och tryck på “Run Launcher”.
6. Servern tar nu en stund på sig att generera artiklar. När du får något liknande “[main] INFO io.javalin.
   Javalin - Javalin started in 244ms \o/” i konsollen så är servern redo.
7. Öppna din webbläsare och gå in på http://localhost:5001/news för att se startsidan där du sedan kan klicka dig 
   vidare. För att använda vårt API följ vår dokumentation för detta på följande länk: https://app.swaggerhub.com/apis/SIMONJERN/ImgNews/

## Om det inte fungerar
- Ibland kan IntelliJ ha svårt att hitta JDK-versionen. Detta brukar kunna lösas genom att man gå in i valfri 
  klass och trycker på felmeddelandet som dyker upp i övre delen av fönstret för att ladda ner rätt version.
- Testa att byta port i klassen "Server". Detta kan vara nödvändigt om du redan har någon annan server eller 
  aplikation som kör på port 5001.