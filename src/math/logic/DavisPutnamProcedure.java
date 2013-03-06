package math.logic;

import java.util.Iterator;

import types.HashList;


public class DavisPutnamProcedure {
	
	public static boolean isSatisfiable(ClauseSet clauseSet){
		return !execute(clauseSet).isEmpty();
	}
	
	private static ClauseSet execute(ClauseSet clauseSet){
		HashList<Clause> tempClauses = new HashList<Clause>();
		HashList<Object> varSet = clauseSet.getItems();
		int count = 1;
		while(count < varSet.size()+1){
			if(clauseSet.isEmpty() || clauseSet.get(0).isEmpty())
				break;
			//Debug.message("variable: "+varSet.get(count-1));
			//Step 1: Remove all clauses from ClauseSet, that contain a literal l and its inverse Âl
			tempClauses.clear();
			for(Clause c1: clauseSet)
				if(c1.hasTwinLiterals())
					tempClauses.add(c1);
			clauseSet.removeAll(tempClauses);
			//Debug.message("(A"+count+") "+clauseSet);
			
			//Step 2: Add all possible resolvents for variable varSet.get(count)
			tempClauses.clear();
			for(int i=0; i<clauseSet.size()-1; i++){
				for(int j=i; j<clauseSet.size(); j++){
					ResolveResult r = clauseSet.get(i).resolve(clauseSet.get(j), varSet.get(count-1));
					if(r!=null){
						tempClauses.add(r.getClause());
					}
				}
			}
			clauseSet.addAll(tempClauses);
			//Debug.message("(B"+count+") "+clauseSet);
			
			//Step 3: Remove all clauses, that contain the variable varSet.get(count)
			tempClauses.clear();
			for(Iterator<Clause> iter=clauseSet.iterator(); iter.hasNext();){
				Clause nextClause = iter.next();
				if(nextClause.containsItem(varSet.get(count-1)))
					tempClauses.add(nextClause);
			}
			clauseSet.removeAll(tempClauses);
			//Debug.message("(C"+count+") "+clauseSet);
			
			count++;
		}
		return clauseSet;
	}

}
