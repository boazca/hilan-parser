# Hilan Parser

This tool creates a spreadsheet out of all the Hilan payslips

Java 8 and Scala 11 is required

## How To Run

### GUI
1. Run Main.scala without arguments
2. Type the domain your company has in hilan. usually https://${company_name}.net.hilan.co.il
3. Enter employee number and password
4. Choose a directory for the payslips
5. press Go! and it will open the directory when it's finished

### Command Line
Run Main.scala file with at least one argument (you can just put -whatever)
- --path => Destination path (folder)
- --company => Company name (Hilan sub domain)
- --username / -u => Employee number 
- --password / -p => Password 
