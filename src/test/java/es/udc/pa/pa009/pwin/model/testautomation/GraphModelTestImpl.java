package es.udc.pa.pa009.pwin.model.testautomation;

public class GraphModelTestImpl implements GraphModelTest {

	@Override
	public void Started() {
		System.out.println("Started");
	}

	@Override
	public void find() {
		System.out.println("find");

	}

	@Override
	public void Eliminated() {
		System.out.println("Eliminated");

	}

	@Override
	public void save() {
		System.out.println("save");

	}

	@Override
	public void initialize() {
		System.out.println("initialize");

	}

	@Override
	public void remove() {
		System.out.println("remove");

	}

	@Override
	public void Created() {
		System.out.println("Created");

	}

}
