# Hilan Parser

This tool creates a spreadsheet out of all your Hilan payslips

## How To Run

Download the project, open it in you favourite IDE and Run `Main.scala`.  
\* Java 8 and Scala 11 is required

Or

Download the fat jar and run it with `java -jar pdfparser-assembly-1.0.jar`

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
