# Hilan Parser

This tool downloads all your Hilan payslips and creates a spreadsheet out of them.

## How To Run

Download the project, open it in your favorite IDE and run `Main.scala`.  
\* Java 11 and Scala 2.13 is required

Or

Download the fat jar and run it from the shell with `java -jar pdfparser-assembly-x.x.x.jar`.

### GUI
1. Run without arguments
2. Type the domain your company has in hilan. usually https://${company_name}.net.hilan.co.il
3. Enter employee number and password
4. Choose a directory for the payslips (you need to create it first)
5. press Go! and it will open the directory when it's finished

### Command Line
Run with at least one argument and it will ask for the rest (you can just put -whatever)
- --path => Destination path (folder)
- --company => Company name (Hilan sub domain)
- --username / -u => Employee number 
- --password / -p => Password 

#### Parsing Without Downloading
If you already have all your payslips and want to parse them to a spreadsheet, run with only the path argument:  
`java -jar pdfparser-assembly-x.x.x.jar --path /Users/you/payslip/folder`.  
\* don't put other documents in the folder
