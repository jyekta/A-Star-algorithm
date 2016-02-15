package algo;

import java.util.ArrayList;

import environment.Cell;
import environment.Entrepot;
import environment.TypeCell;
import gui.EnvironmentGui;

/** this class propose an implementation of the a star algorithm */
public class AlgoAStar {

	/** nodes to be evaluated*/
	ArrayList<Cell> freeNodes;
	/** evaluated nodes*/
	ArrayList<Cell> closedNodes;

	/** Start Cell*/
	Cell start;
	/** Goal Cell*/
	Cell goal;

	/** graphe / map of the nodes*/
	Entrepot ent;
	/** gui*/
	EnvironmentGui gui;

	/**initialize the environment (100 x 100 with a density of container +- 20%) */
	AlgoAStar()
	{
		ent = new Entrepot(50, 50, 0.2, this);
		gui = new EnvironmentGui(ent);
		reCompute();
	}

	/** a* algorithm to find the best path between two states 
	 * @param _start initial state
	 * @param _goal final state*/
	ArrayList<Cell> algoASTAR(Cell _start, Cell _goal)
	{
		start = _start;
		goal = _goal;
		// list of visited nodes
		closedNodes = new ArrayList<Cell>();
		// list of nodes to evaluate
		freeNodes = new ArrayList<Cell>();
		freeNodes.add(start);
		// no cost to go from start to start
		// TODO: g(start) <- 0
		start.setG(0);
		// TODO: h(start) <- evaluation(start)
		start.setH(evaluation(start));
		// TODO: f(start) <- h(start)
		start.setF(start.getH());
		// while there is still a node to evaluate
		while(!freeNodes.isEmpty())
		{
			// choose the node having a F minimal
			Cell n = chooseBestNode();
			// stop if the node is the goal
			if (isGoal(n)) return rebuildPath(n);
			// TODO: freeNodes <- freeNodes - {n}
			freeNodes.remove(n);
			// TODO: closedNodes <- closedNodes U {n}
			closedNodes.add(n);
			// construct the list of neighbourgs
			ArrayList<Cell> nextDoorNeighbours = neighbours4(n);
			for(Cell ndn:nextDoorNeighbours)
			{
				// if the neighbour has been visited, do not reevaluate it
				if (closedNodes.contains(ndn))
					continue;
				// cost to reach the neighbour is the cost to reach n + cost from n to the neighbourg
				//TODO: int cost = ...
				int cost = n.getG() + costBetween(n,ndn);
				boolean bestCost = false;
				// if the neighbour has not been evaluated
				if (!freeNodes.contains(ndn))
				{
					// TODO: freeNodes <- freeNodes U {ndn}
					freeNodes.add(ndn);
					// TODO: h(ndn) -> evaluation(ndn)
					ndn.setH(evaluation(ndn));
					bestCost = true;
				}
				else
					// if the neighbour has been evaluated to a more important cost, change its evaluation
					if (cost < ndn.getG())
						bestCost = true;
				if(bestCost)
				{
					ndn.setParent(n);
					//TODO : g(ndn) <- cost
					ndn.setG(cost);
					//TODO : f(ndn) <- g(ndn) + h(ndn)
					ndn.setF(ndn.getG()+ndn.getH());
				}
			}
		}
		return null;
	}

	/** return the path from start to the node n*/
	ArrayList<Cell> rebuildPath(Cell n)
	{
		if (n.getParent()!=null)
		{
			ArrayList<Cell> p = rebuildPath(n.getParent());
			n.setVisited(true);
			p.add(n);
			return p;
		}
		else
			return (new ArrayList<Cell>());
	}

	/** algo called to (re)launch a star algo*/
	public void reCompute()
	{
		ArrayList<Cell>  solution = algoASTAR(ent.getStart(), ent.getGoal());
		ent.setSolution(solution);
		if (solution==null) 
			System.out.println("solution IMPOSSIBLE");
		gui.repaint();
	}
	

	/** return the estimation of the distance from c to the goal*/
	int evaluation(Cell c)
	{		
		// TODO: cf cours : sur Terre : 10* distance vol d'oiseau entre but(goal) et c
		 int dx2 = (goal.getX()-c.getX())*(goal.getX()-c.getX());
		int dy2 = (goal.getY()-c.getY())*(goal.getY()-c.getY());

		return 10* (int)Math.sqrt(dx2+dy2);



	}
	// Manhatan
	/*int evaluation(Cell c)
	{
		// TODO: cf cours : sur Terre : 10* distance vol d'oiseau entre but(goal) et c
		int dx2 = (goal.getX()-c.getX())+(goal.getX()-c.getX());
		int dy2 = (goal.getY()-c.getY())+(goal.getY()-c.getY());

		return 10* (int)Math.sqrt(dx2+dy2);



	}*/

	/** return the free node having the minimal f*/
	Cell chooseBestNode()
	{
		//TODO...
		int minF=-1;
		Cell minf_node = null;
		for(Cell nfn:freeNodes) {
			if(minF == -1 || nfn.getF() < minF) {
				minF = nfn.getG()+nfn.getH();
				minf_node = nfn;
			}
		}
		return minf_node;
	}

	/** return whether n is a goal or not */
	boolean isGoal(Cell n)
	{
		return (n.getX() == goal.getX() && n.getY() == goal.getY());
	}

	/** return the neighbouring of a node n; a diagonal avoid the containers */
	ArrayList<Cell> neighbours(Cell n)
	{
		// TODO: (en reponse au 3e cas)
		int x = n.getX();
		int y = n.getY();
		ArrayList<Cell> results = new ArrayList<Cell>();

		for (int i=-1; i<2; i++) {
			for (int j=-1; j<2; j++) {
				Cell c = ent.getCell(x+i,y+j);
				if (ent.inBox(x+i,y+j) && (i!=0 || j!=0) && !c.isContainer()) {
					results.add(c);
				}
			}
		}
		return results;
	}

	/** return the neighbouring of a node n*/
	ArrayList<Cell> neighboursDiag(Cell n)
	{
		// TODO: (en reponse au 1er cas)
		int x = n.getX();
		int y = n.getY();
		ArrayList<Cell> results = new ArrayList<Cell>();

		for (int i=-1; i<2; i=i+2) {
			for (int j=-1; j<2; j=j+2) {
				Cell c = ent.getCell(x+i,y+j);
				if (ent.inBox(x+i,y+j) && !c.isContainer()) {
					results.add(c);
				}
			}
		}
		return results;
	}

	/** return the neighbouring of a node n without permission to go in diagonal*/
	ArrayList<Cell> neighbours4(Cell n)
	{
		// TODO: (en reponse au 2e cas)
		int x = n.getX();
		int y = n.getY();
		ArrayList<Cell> results = new ArrayList<Cell>();

		for (int i=-1; i<2; i++) {
			for (int j=-1; j<2; j++) {
				Cell c = ent.getCell(x+i,y+j);
				if (ent.inBox(x+i,y+j) && (i==0 ^ j==0) && !c.isContainer()) {
					results.add(c);
				}
			}
		}
		return results;
	}

	/** return the cost from n to c : 10 for a longitudinal move, 14 (squareroot(2)*10) for a diagonal move */
	int costBetween(Cell n, Cell c)
	{	
		//TODO : sur terre, deplacement horizontal ou vertical = 10; en diagonale = 14
		if(n.getX() == c.getX() || n.getY() == c.getY()) {
			return 10;
			//return 10; pour diagonal
			//return 1; pour horisontal vertical
		} else {
			return 14;
		}
	}


	public static void main(String []args)
	{
		new AlgoAStar();

	}
}
