import java.util.Scanner;

public class Main {

    public static void main(String[] args){
        try(Scanner scanner=new Scanner(System.in)){
            if(args.length!=1){
                throw new IllegalArgumentException("Bitte Programm mit Argument starten.");
            }
            String filepath=args[0];
            Net net= new Net(null,null);
            net.parser(filepath);

            while(true){
                System.out.println();
                System.out.println("Bitte geben sie einen der Befehle ein:");
                System.out.println("'shoot name' \t> feuert genannte Transition\n'shoot number' \t> feuert x-mal Transitionen\n'print' \t> druckt Petri-Netz\n'makeMatrix' \t> druckt Matrix\n'firstLoaded' \t> druckt erste geladene Transition\n'reachable'\t> druckt den Erreichbarkeitsbaum\n'toDot'\t\t> gibt DOT String zurück\n'dotFile'\t> herunterladen der DOT-Datei\n'exit|stop' \t> beende das Programm");
                System.out.println();
                System.out.println("Geladene Transitionen:");
                System.out.println(net.loaded());
                String input=scanner.nextLine().trim();
                if(input.isEmpty()){
                    continue;  
                }
                String[] line=input.split("\\t");
                
                String argument=line[0].trim();
        
                if(argument.equals("stop")||argument.equals("exit")){
                    break;
                }
                switch(argument){
                    case "print":
                        System.out.println(net.toString());
                        break;
                    case "shoot":
                        if(line.length!=2){
                            System.out.println("Bitte bei shoot eine Zahl oder einen Namen mit angeben.");
                            continue;
                        }
                        String nameOrInt=line[1];
                        try{
                            int zahl=Integer.parseInt(nameOrInt);
                            net.shoot(zahl); 
                            break;                   
                        }
                        catch(NumberFormatException e){
                            net.shoot(nameOrInt);
                            break;
                        }
                    case "firstLoaded":
                        System.out.println(net.firstLoaded());
                        break;
                    case "loaded":
                        if(line.length<2){
                            System.out.println(net.loaded());
                            break;
                        }
                        else{
                            System.out.println(net.loaded(line[1]));
                            break;
                        }
                    case "makeMatrix":
                        net.makeMatrix();
                        break;
                    case "reachable":
                    	System.out.println(net.reachable());
                        break;
                    case "toDot":
                        System.out.println(net.toDot());
                        break;
                    case "dotFile":
                        if(line.length<2){
                            System.out.println("Bitte noch den Speicher-path angeben.");
                            continue;
                        }
                        else{
                            if(net.dotFile(line[1].trim())){
                                System.out.println("Datei erfolgreich heruntergeladen.");
                            }
                        }
                        break;
                    case"solve":
                        net.solve();

                    default:
                        System.out.println("Bitte einen gültigen Befehl eingeben");
                        break;
                }
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
