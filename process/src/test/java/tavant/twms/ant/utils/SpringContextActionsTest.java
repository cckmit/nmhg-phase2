package tavant.twms.ant.utils;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;



public class SpringContextActionsTest extends TestCase {
	private static class MockContext implements ApplicationContext {
		public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
			// TODO Auto-generated method stub
			return null;
		}

		public String getDisplayName() {
			// TODO Auto-generated method stub
			return null;
		}

		public ApplicationContext getParent() {
			// TODO Auto-generated method stub
			return null;
		}

		public long getStartupDate() {
			// TODO Auto-generated method stub
			return 0;
		}

		public Resource[] getResources(String arg0) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		public void publishEvent(ApplicationEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		public String getMessage(MessageSourceResolvable arg0, Locale arg1) throws NoSuchMessageException {
			// TODO Auto-generated method stub
			return null;
		}

		public String getMessage(String arg0, Object[] arg1, Locale arg2) throws NoSuchMessageException {
			// TODO Auto-generated method stub
			return null;
		}

		public String getMessage(String arg0, Object[] arg1, String arg2, Locale arg3) {
			// TODO Auto-generated method stub
			return null;
		}

		public ClassLoader getClassLoader() {
			// TODO Auto-generated method stub
			return null;
		}

		public Resource getResource(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean containsBeanDefinition(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		public int getBeanDefinitionCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		public String[] getBeanDefinitionNames() {
			// TODO Auto-generated method stub
			return null;
		}

        public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
            return null;
        }

        public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) {
            return null;
        }

        public String[] getBeanNamesForType(Class arg0, boolean arg1, boolean arg2) {
			// TODO Auto-generated method stub
			return null;
		}

		public String[] getBeanNamesForType(Class arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public Map getBeansOfType(Class arg0, boolean arg1, boolean arg2) throws BeansException {
			// TODO Auto-generated method stub
			return null;
		}

		public Map getBeansOfType(Class arg0) throws BeansException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean containsLocalBean(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		public BeanFactory getParentBeanFactory() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean containsBean(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		public String[] getAliases(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public Object getBean(String arg0, Class arg1) throws BeansException {
			// TODO Auto-generated method stub
			return null;
		}

		public Object getBean(String arg0) throws BeansException {
			// TODO Auto-generated method stub
			return null;
		}

        public <T> T getBean(Class<T> requiredType) throws BeansException {
            return null;
        }

        public Class getType(String arg0) throws NoSuchBeanDefinitionException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isSingleton(String arg0) throws NoSuchBeanDefinitionException {
			// TODO Auto-generated method stub
			return false;
		}
		
		public boolean isTypeMatch(String str, Class cls) {
			return false;
		}

		public String getId() {
			// TODO Auto-generated method stub
			return null;
	}

		public Object getBean(String arg0, Object[] arg1) throws BeansException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isPrototype(String arg0) throws NoSuchBeanDefinitionException {
			// TODO Auto-generated method stub
			return false;
		}

        public Environment getEnvironment() {
            return null;
        }
    }

	private SpringContextActions fixture;
	public void setUp() {
		final String contextResources = "mockContext";
		fixture = new SpringContextActions() {

			@Override
			protected ApplicationContext constructContext(String[] appContextResources) {
				assertEquals(1,appContextResources.length);
				assertEquals(contextResources,appContextResources[0]);
				return new MockContext();
			}
			
		};
		fixture.setAppContextLocation(contextResources);
	}
	
	public void testExecute() {
		final ActionInput input = new ActionInput();
		input.setName("property");
		input.setValue("value");
		
		ActionDefinition actionDefinition = new ActionDefinition();
		actionDefinition.setClassName(MockTask.class.getName());
		actionDefinition.getInputs().add(input);
		
	
		fixture.addConfiguredAction(actionDefinition);
		fixture.execute();
		
		MockTask mockTask = ((MockTask)fixture.getActions().get(0));
		assertEquals("value",mockTask.getProperty());
		assertEquals(1,mockTask.getState().size());
	}

	public void testAddConfiguredAction_clean() {
		final ActionInput input = new ActionInput();
		input.setName("property");
		input.setValue("value");
		
		ActionDefinition actionDefinition = new ActionDefinition();
		actionDefinition.setClassName(MockTask.class.getName());
		actionDefinition.getInputs().add(input);
		
		assertTrue(fixture.getActions().isEmpty());
		fixture.addConfiguredAction(actionDefinition);
		assertEquals("value",((MockTask)fixture.getActions().get(0)).getProperty());
	}

	public void testAddConfiguredAction_error() {
		final ActionInput input = new ActionInput();
		input.setName("property");
		input.setValue("value");
		
		ActionDefinition actionDefinition = new ActionDefinition();
		actionDefinition.setClassName(MockTask.class.getName()+"$");
		actionDefinition.getInputs().add(input);
		
		Exception ex = null;
		try {
			fixture.addConfiguredAction(actionDefinition);
		} catch (RuntimeException e) {
			ex = e;
		}
		assertNotNull(ex);
		assertTrue(fixture.getActions().isEmpty());
	}
}
