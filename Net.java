import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;



	public class Net implements Cloneable {
		private HashMap<String,Transition> transitions;
		private HashMap<String,Place> places;
		//Vergleichsmaps zur Identifikation der placeen / Transitionen in Matrixdarstellung
       	private Map<String, Integer> identifyPos;
        private Map<String, Integer> identifyTrans;


	public Net(HashMap<String,Transition> transitions,HashMap<String,Place> places) {
		this.transitions = (transitions == null) ? new HashMap<>() : transitions;
		this.places = (places == null) ? new HashMap<>() : places;
	}

	public void parser(String path){
		try(BufferedReader reader= new BufferedReader(new FileReader(path));){ //BufferedReader für das Lesen der Eingabedatei
			String line=reader.readLine();
			if(line==null){						//wenn die Datei leer ist Exception werfen
				throw new IllegalArgumentException("Bitte etwas eingeben.");
			}
			if(!(line.trim().equals("Places:"))){		//wenn die erste Zeile nicht:"place:" ist, dann Exception werfen
				throw new IllegalArgumentException("Bitte 'Places:' angeben.");
			}
			line=reader.readLine();		//erste Zeile überspringen
			while(line!=null){
				line=line.trim();			//Leerzeichen vorne und hinten entfernen
				if(line.equals("Edges:")){	//sobald die Transitionen anfangen, Schleife abbrechen
					break;
				}
				if(line.isEmpty()){			//	leere Zeilen überspringen
					line=reader.readLine();
					continue;
				}
				String[] pos=line.split("\\t" );		//Zeile aufsplitten in Name und Markenanzahl
				if(pos.length!=2){						//wenn mehr oder weniger angegeben ist Exception werfen
					throw new IllegalArgumentException("Bitte Namen und Markenanzahl der places aufzählen.");
				}
				String posName=pos[0].trim(); 	//Name der place
				if(places.containsKey(posName)){		//prüfen, ob die place bereits angegeben wurde
					throw new IllegalArgumentException("Die place kam bereits vor.");
				}
				else{		//place hinzufügen
					places.put(posName, new Place(posName,Integer.parseInt(pos[1].trim())));
				}
				line=reader.readLine();
			}
			if (line==null) {
				throw new IllegalArgumentException("Es fehlt 'Transitions:'.");
			}
			if(!(line.trim().equals("Edges:"))){		//gucken ob die Zeile:"Transitions:" wirklich als nächstes kommt
				throw new IllegalArgumentException("Bitte Transitions angeben.");
			}
			line=reader.readLine();
			while(line!=null){		//weiter mit den Transitionen
				line=line.trim();
				if(line.isEmpty()){		//leere Zeilen überspringen
					line=reader.readLine();
					continue;
				}
				String[] lines=line.split("\\t");	//Zeile aufsplitten in Transition, give/take, place und Kantengewicht
				if(lines.length!=4){		//wenn mehr oder weniger angegeben wurde Exception werfen
					throw new IllegalArgumentException("Bitte Transition, Weight und place in der richtigen Reihenfolge angeben.");
				}
				String transName=lines[0].trim();	//Transitionsname
				Transition trans;		
				if(transitions.containsKey(transName)){		//überprüfen, ob die Transition bereits vorkam
					trans=transitions.get(transName);
				}
				else{
					trans=new Transition(transName);		//neue Transition erstellen, falls sie noch nicht vorkam und hinzufügen
					transitions.put(transName, trans);
				}
				Place place=places.get(lines[2].trim());
				if(place==null){		//gucken, ob es die place wirklich gibt
					throw new IllegalArgumentException("Die angegebene place gibt es nicht.");
				}
				String typ = lines[1].trim();
				if (!typ.equals("take") && !typ.equals("give")) {
   					throw new IllegalArgumentException("Bitte 'take' oder 'give' angeben.");
				}
				if(typ.equals("give")){		//überprüfen, ob es eine ausgehende Kante ist oder Eingehende
					if(trans.addGive(new Give(Integer.parseInt(lines[3].trim()),place))!=true){ //Kante der Transiton hinzufügen
						throw new IllegalArgumentException("Die Kante gibt es bereits.");
					}
				}
				else{
					if(trans.addTake(new Take(Integer.parseInt(lines[3].trim()),place))!=true){  // Kante hinzufügen
						throw new IllegalArgumentException("Die Kante gibt es bereits.");
					}
				}
				line=reader.readLine();
			}

		}
		catch(FileNotFoundException x){				//alle Exceptions fangen
			System.err.println("Bitte richtigen Dateipfad angeben!");
		}
		catch(NumberFormatException e){
			System.err.println("Bitte einen ganzzahloigen Wert angeben.");
		}
		catch(IOException e){
			System.err.println("Anderer Fehler.");
		}
		catch(IllegalArgumentException e){
			System.err.println(e.getMessage());
		}
	}

	public String toDot(){						//einen String für graphische Darstellung
		StringBuilder graph=new StringBuilder();
		graph.append("digraph G{\n");			//bipartiter Graph
		graph.append("rankdir=LR\n");			//von links nach rechts
		
		graph.append(" node [shape=circle]");		//alle places sind Kreise
		for(Place p:places.values()){				//durch alle places iterieren
			String id="P_ID"+dotID(p.getName());
			String label=p.getName()+"("+p.getToken()+")";
			graph.append(id);
			graph.append("[label=\"");
			graph.append(label);
			graph.append("\"]\n");
		}

		graph.append(" node [shape=box, style=filled] ");	//alle Transitions als Boxen darstellen
		for(Transition t:transitions.values()){				//drüber iterieren
			String id="T_ID"+dotID(t.getName());
			String label=t.getName();
			if(t.loaded()){									//falls die Transition geladen ist, die Box grün ausfüllen
				graph.append(id);
				graph.append("[label=\"");
				graph.append(label);
				graph.append("\",");
				graph.append(" fillcolor=\"");
				graph.append("lightgreen\"];\n");
			}
			else{
				graph.append(id);
				graph.append("[label=\"");
				graph.append(label);
				graph.append(	"\"];\n");
			}
		}

		for(Transition t:transitions.values()){
			String id="T_ID"+dotID(t.getName());
			for(Take take:t.getTakes()){
				String pos="P_ID"+dotID(take.getPlaceFrom().getName());
				graph.append(pos);
				graph.append("->");
				graph.append(id);
				graph.append("[label=\"");
				graph.append(take.getWeight());
				graph.append("\"];\n");
			}
			for(Give give:t.getGives()){
				String pos="P_ID"+dotID(give.getPlaceTo().getName());
				graph.append(id);
				graph.append("->");
				graph.append(pos);
				graph.append("[label=\"");
				graph.append(give.getWeight());
				graph.append("\"];\n");
			}
		}
		graph.append("}");
		return graph.toString();
	}

	public String dotID(String id){
		String s = id.replaceAll("[^A-Za-z0-9_]", "_");
		return s.trim();

	}

	public boolean dotFile(String path) {    //erstellen einer DOT-Datei
    	try(FileWriter file = new FileWriter(path)) {
        	file.write(toDot()); 
			return true;  
    	}
    	catch (IOException e) {
       		System.err.println("Fehler beim Erstellen der DOT-Datei.");
			return false;
    	}
	}


	public ArrayList<String> loaded() {
		ArrayList<String> out = new ArrayList<>();

		Iterator<Map.Entry<String, Transition>> iter = transitions.entrySet().iterator();

		Map.Entry<String, Transition> curr;

		while(iter.hasNext()) {
			curr = iter.next();
			if(curr.getValue().loaded()) {
				out.add(curr.getKey());
			}
		}

		return out;
	}

	public String firstLoaded() {

		Iterator<Map.Entry<String, Transition>> iter = transitions.entrySet().iterator();

		Map.Entry<String, Transition> curr;

		while(iter.hasNext()) {
			curr = iter.next();
			if(curr.getValue().loaded()) {
				return curr.getKey();
			}
		}

		return null;
	}


	public boolean loaded(String name) {
		return transitions.get(name).loaded();
	}


	public int shoot(int times) {
		Random rand = new Random();
		for (int i = 0; i<times;i++) {
			ArrayList<String> loaded = loaded();
				if(loaded.isEmpty()) {
					return (times-i);
				}
			shoot(loaded.get(rand.nextInt(loaded.size())));
		}
		return 0;
	}

	public boolean shoot(String name) {
		return transitions.get(name).shoot();
	}


	public String toString() {
		StringBuilder sb = new StringBuilder();

		Iterator<Map.Entry<String, Transition>> iterT = transitions.entrySet().iterator();
		Iterator<Map.Entry<String, Place>> iterP = places.entrySet().iterator();

		Map.Entry<String, Transition> currT;
		//TODO hier das einfügen eines Tabellenkopfes in den StringBuilder einfügen

		while(iterP.hasNext()) {
			sb.append(iterP.next().getValue().toString());
			sb.append('\n');
		}
		
		while(iterT.hasNext()) {
			currT = iterT.next();
			sb.append(currT.getValue().givesToString());
			sb.append(currT.getValue().takesToString());
			sb.append('\n');
		}

		return sb.toString();
	}

	public Tree reachable() {
		TreeNode root = new TreeNode(null, places);
		Tree out = new Tree(root);
		HashSet<HashMap<String,Place>> done = new HashSet<HashMap<String,Place>>();
		reachable(clone(),done, root);
		return (out);
	}

	
	private static void reachable(Net in, HashSet<HashMap<String,Place>> done, TreeNode parent){ 
		if(done.contains(in.places)) {
			return;
		}

		ArrayList<String> toDoList = in.loaded();



		if (toDoList.isEmpty()) {
			return;
		}


		Iterator<String> toDo = toDoList.iterator();

		Net curr;
		TreeNode newChild;
		
		while(toDo.hasNext()) {
			curr = in.clone();
			curr.shoot(toDo.next());
			done.add(curr.places);
			newChild = new TreeNode(parent, curr.places);
			parent.addChild(newChild);
			Net.reachable(curr, done, newChild);
		}
	}




	public Net clone() {
		HashMap<String,Transition> newTransitions = new HashMap<String,Transition>(transitions.size());
		HashMap<String,Place> newplaces = new HashMap<String,Place>(places.size());

		Iterator<Map.Entry<String, Transition>> iterT = transitions.entrySet().iterator();
		Iterator<Map.Entry<String, Place>> iterP = places.entrySet().iterator();

		Transition currT;
		Place currP;


		ArrayList<Take> currTakes;
		ArrayList<Give> currGives;
		Transition newTransition;

		while(iterP.hasNext()) {
			currP = iterP.next().getValue();
			newplaces.put(currP.getName(),currP.clone());
		}

		while(iterT.hasNext()) {
			currT = iterT.next().getValue();
			newTransition = new Transition(currT.getName());

			currGives = currT.getGives();
			for(Give currGive: currGives) {
				currP = currGive.getPlaceTo();
				newTransition.addGive(new Give(currGive.getWeight(), newplaces.get(currP.getName())));
			}

			currTakes = currT.getTakes();
			for(Take currTake: currTakes) {
				currP = currTake.getPlaceFrom();
				newTransition.addTake(new Take(currTake.getWeight(), newplaces.get(currP.getName())));
			}
			newTransitions.put(newTransition.toString(), newTransition);

		}

		return(new Net(newTransitions, newplaces));
	}

	private boolean equalToken(Place in) {
		if (places.get(in.getName())==null) {
			return false;
		} else {
		return places.get(in.getName()).getToken()==in.getToken();
		}

	}


	public boolean equalToken(Net in) {
		Iterator<Map.Entry<String, Place>> iter = places.entrySet().iterator();

		Map.Entry<String, Place> curr;

		while(iter.hasNext()) {
			curr = iter.next();
			if(!in.equalToken(curr.getValue())) {
				return false;
			}
		}
		return true;
	}

		
	// Erstellen der Matrixdarstellung für spätere Berechnung der Invarianten
    public double[][] makeMatrix(){

		this.identifyPos = buildMap(this.places.keySet());
		this.identifyTrans = buildMap(this.transitions.keySet());

		//Nullmatrix erstellen
        double[][] matrix= new double[identifyPos.size()][identifyTrans.size()];

		//Zähler, der aktuelle place der Transition in Matrix anzeigt
		int currTransitionIndex = 0; 
		//Schleife die durch alle Transitionen geht
        for (Transition currTransition : transitions.values()){
			//Schleife, die über alle Take(s) in ArrayList<Take> iteriert
			for(Take currTake: currTransition.getTakes()){
				//Zwischenvariablen zur Übersichtlichkeit bei Bestimmung Index der place und Gewicht der Kante
				Place currplace = currTake.getPlaceFrom();
				String currplaceName = currplace.getName();
				//Identifikation Index der place in Matrix
				int indexCurrplace = identifyPos.get(currplaceName);
				//Initialisierung Matrix an entsprechender Stelle, negatives Gewicht da eingehende Kante an Transition (source)
				matrix[indexCurrplace][currTransitionIndex] = - currTake.getWeight();
			}

			//Schleife iteriert über alle Gives in ArrayList<Give>
			for(Give currGive: currTransition.getGives()){
				//Zwischenvariablen
				Place currplace = currGive.getPlaceTo();
				String currplaceName = currplace.getName();
				//Identifikation Index der place in Matrix
				int indexCurrplace = identifyPos.get(currplaceName);
				//Initialisierung Matrix
				matrix[indexCurrplace][currTransitionIndex] = currGive.getWeight();
			}

			currTransitionIndex++;
		
		}
		
		printMatrix(matrix);

        return matrix;
    }
    
	//Hilfsmethode zum bauen der IndizeMaps
    public Map<String, Integer> buildMap (Set<String> keys){
        Map<String, Integer> map = new HashMap<>();
        int i = 0; 
		//Schleife läugt über alle Schlüssel in Maps und speichert diese gemeinsam mit Index in neuer Map
        for(String key : keys){
            map.put(key, i++);
        }
        return map;

    }
	//Hilfsmwthode, die Matrix auf Konsole ausgibt
	public void printMatrix (double[][] matrix){
		for (double[] m1 : matrix) {
            for (double num : m1) {
                System.out.print(num + " ");
            }
			System.out.println();
        }
		System.out.println();
	}

	// noch nicht vollständig
	public int[] solve(){

		double[][] m = makeMatrix();
		int rows = m.length;
		int cols = m[0].length;
		int [] x = new int [rows];
		int[] pivots = new int[rows];


		/*for (int i=0; i < rows; i++){
			for(int j = 0; j< cols; j++){
				m[i][j] = matrix[i][j];
			}
		}*/
		//spaltenweise über Array laufen
		for (int p = 0; p <Math.min(rows, cols); p ++){
			int currRow = p;
			//ab aktueller Zeile durchgehen, bis Zahl in p-ter Spalte != 0
			for ( ;currRow < rows && m[currRow][p] == 0; currRow++){
			}
			//wenn Zelle ungleich 0 -> weiter
			if(m[currRow][p]!=0){
				//wenn Zeile ungleich p-ter Zeile, dann Zeile mit p-ter Zeile tauschen
				if(currRow < rows){
					for(int i = p; i < cols; i++){
						double temp = m[p][i];
						m[p][i] = m[currRow][i];
						m[currRow][i] = temp;
					}

				}
				//alle Einträge in p-ter Spalte unter p-ter Zeile Null setzen
				for(int i = p+1; i<rows; i++){
					if(m[i][p] != 0){
						double factor = m[i][p] / m[p][p] * (-1);
						for(int j = p; j<cols; j++){
							m[i][j] += factor * m[p][j] ;
						}
					}
				}
				printMatrix(m);
				pivots[p]= p;
			}
		}
		
		return x ;
	}
		

}






