package es.udc.pa.pa009.pwin.model.testautomation;

import java.nio.file.Path;
import java.nio.file.Paths;

//@GraphWalker(value = "random(edge_coverage(100) or time_duration(60))", start = "initialize")
public class GraphModelTestImpl /*
								 * extends ExecutionContext implements
								 * GraphModelTest
								 */ {
	public final static Path MODEL_PATH = Paths.get("es/udc/pa/pa009/pwin/model/testautomation/GraphModelTest.graphml");
	/*
	 * @Override public void Started() { System.out.println("Started"); }
	 * 
	 * @Override public void find() { System.out.println("find");
	 * 
	 * }
	 * 
	 * @Override public void Eliminated() { System.out.println("Eliminated");
	 * 
	 * }
	 * 
	 * @Override public void save() { System.out.println("save");
	 * 
	 * }
	 * 
	 * @Override public void initialize() { System.out.println("initialize");
	 * 
	 * }
	 * 
	 * @Override public void remove() { System.out.println("remove");
	 * 
	 * }
	 * 
	 * @Override public void Created() { System.out.println("Created");
	 * 
	 * }
	 */
}