package tavant.twms.fit.infra;

import fitnesse.fixtures.TableFixture;

/**
 * This class is used to set the Spring configuration for a Fitnesse page.
 * You add a table with this fixture to a Fitnesse page and define the
 * file names of all Spring configuration files.
 */
public class CreateSpringContextFromFiles extends TableFixture {

    /**
     * Set the current ApplicationContext in the ApplicationContextHolder.
     * @param the number of rows
     */
    @Override
    protected void doStaticTable(int rows) {
        String[] configLocations = new String[rows]; 
        for (int i = 0; i<rows; i++) {
            configLocations[i] = getText(i,0);
        }
        ApplicationContextHolder.getApplicationContextHolder().initializeContext(configLocations);        
    }

}

