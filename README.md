# ImgNews

## Instruktioner
1. Börja med att ladda ner eller klona projektet från GitHub.
2. Öppna projektet i IntelliJ IDEA.
3. Vi behöver nu lägga in aktuella API-nycklar. Skapa en fil i huvudmappen "ImgNews" som du döper till 
   "api_keys.txt". Detta kan du göra genom att högerklicka på ImgNews-mappen i projektvyn och välja "New" -> 
   "File". 
4. Be om nycklar från utvecklarna (finns separat i rapporten) eller skapa egna nycklar på:
   - https://newsapi.org/
   - https://beta.openai.com
5. Fyll i nycklarna i filen du precis skapade enligt följande format (byt ut "nyckel" mot din nyckel):
   
   openai="nyckel"
   
   newsapi="nyckel"

6. Hur du startar själva programmet kan variera sig mellan operativsystem men generellt sett vill du gå in i 
   klassen "Launcher" och köra main-metoden därifrån. 
7. Servern tar nu en stund på sig att generera artiklar. När du får något liknande “[main] INFO io.javalin.
   Javalin - Javalin started in 244ms \o/” i konsollen så är servern redo. Notera att ett antal "Exceptions" 
   kan uppstå medan programmet skapar artiklar, detta är helt normalt och inget att bekymra sig över.
8. Öppna din webbläsare och gå in på http://localhost:5001/news för att se startsidan där du sedan kan klicka dig 
   vidare enligt egna önskemål. För att använda vårt API följ vår dokumentation för detta på följande länk: 
   https://app.swaggerhub.com/apis/SIMONJERN/ImgNews/

## Om det inte fungerar
- Ibland kan IntelliJ ha svårt att hitta JDK-versionen. Detta brukar kunna lösas genom att man gå in i valfri 
  klass och trycker på felmeddelandet som dyker upp i övre delen av fönstret för att ladda ner rätt version.
- Testa att byta port i klassen "Server". Detta kan vara nödvändigt om du redan har någon annan server eller 
  aplikation som kör på port 5001.
