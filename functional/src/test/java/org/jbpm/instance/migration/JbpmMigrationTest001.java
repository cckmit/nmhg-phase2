package org.jbpm.instance.migration;

public class JbpmMigrationTest001 implements Migration {

	public StateNodeMap createNodeMap() {

		 return new StateNodeMap(new String[][]{{"ThirdNode", "NewThirdNode"},{"PreFirstNode", "PreFirstNode2"}});

	}

}
