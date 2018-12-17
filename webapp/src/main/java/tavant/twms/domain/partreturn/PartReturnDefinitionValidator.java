package tavant.twms.domain.partreturn;

import java.util.List;

import org.springframework.util.Assert;

import tavant.twms.domain.common.Duration;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.ValidatorSupport;

public class PartReturnDefinitionValidator extends ValidatorSupport {

    private PartReturnDefinition partReturnDefinition;

    public void validate(Object object) throws ValidationException {
        this.partReturnDefinition = ((PartReturnDefinition) object);
        Assert.notNull(partReturnDefinition);
        List<PartReturnConfiguration> configurations = partReturnDefinition.getConfigurations();
        validatePartReturnDates(configurations);
    }

    private void validatePartReturnDates(List<PartReturnConfiguration> configurations) {
        int noOfConfigs = configurations.size();
        // Till date cannot be in the past at the time PRD is created.
        // However, it can take a past date for subsequent modifications.
        for (PartReturnConfiguration partReturnConfiguration : configurations) {
            CalendarDate tillDate = partReturnConfiguration.getDuration().getTillDate(); 
            CalendarDate fromDate = partReturnConfiguration.getDuration().getFromDate();
            if(tillDate == null || fromDate ==null ||
            		partReturnConfiguration.getDueDays() == null || partReturnConfiguration.getDueDays().intValue() <= 0){
            	return;
            }
        }

        // If more than 1 configurations exist on a PRD, check to see that the
        // durations are continuous. Skip check if only one PRC exists.
        if (noOfConfigs == 1) {
        	CalendarDate startDate = configurations.get(0).getDuration().getFromDate();
        	CalendarDate endDate = configurations.get(0).getDuration().getTillDate();
        	if (startDate != null && endDate != null) {
				if (startDate.isAfter(endDate))
					getValidatorContext().addActionError(
							getValidatorContext().getText(
									"error.manageRates.endDateBeforeStartDate",
									new String[] {
											startDate.toString("MM/dd/yyyy"),
											endDate.toString("MM/dd/yyyy") }));
			}        	
        	return;
        }

        for (int i = 1; i < noOfConfigs; i++) {
            Duration thisPRDuration = configurations.get(i).getDuration();
            Duration prevPRDuration = configurations.get(i - 1).getDuration();
                        
            CalendarDate preStartDate = prevPRDuration.getFromDate();
            CalendarDate prevEndDate = prevPRDuration.getTillDate();
            CalendarDate currentStartDate = thisPRDuration.getFromDate();
            CalendarDate currentEndDate = thisPRDuration.getTillDate();
            
            if((preStartDate != null && prevEndDate != null) || (currentStartDate != null && currentEndDate != null)){            
	            if(preStartDate.isAfter(prevEndDate) ||  currentStartDate.isAfter(currentEndDate) )
	            	getValidatorContext().addActionError(
	                        getValidatorContext().getText("error.manageRates.endDateBeforeStartDate",
	                                new String[] { prevEndDate.toString("MM/dd/yyyy"),
	                                        currentStartDate.toString("MM/dd/yyyy") }));
            }
            	           	
            if ((prevEndDate != null && currentStartDate != null) 
            		&& !(prevEndDate.plusDays(1).equals(currentStartDate))) {
                getValidatorContext().addActionError(
                        getValidatorContext().getText("error.partReturnConfiguration.noGapsInConsecutiveDateRange",
                                new String[] { prevEndDate.toString("MM/dd/yyyy"),
                                        currentStartDate.toString("MM/dd/yyyy") }));
            }
        }
    }

    public PartReturnDefinition getPartReturnDefinition() {
        return partReturnDefinition;
    }
}
