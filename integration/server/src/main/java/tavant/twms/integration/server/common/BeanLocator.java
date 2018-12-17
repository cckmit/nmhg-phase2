/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.server.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 *
 * @author prasad.r
 */
public class BeanLocator implements BeanFactoryAware{
    
    private static final Map<String,BeanFactory> beanFactories = new HashMap<String,BeanFactory>();

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        addToMap(beanFactory);
    }
    
    public Object getBean(String beanName){
        Iterator<String> it = beanFactories.keySet().iterator();
        Object bean = null;
        while (it.hasNext()) {
            String s = it.next();
            try{
                bean = beanFactories.get(s).getBean(beanName);
            }catch (Exception e){}
        }
        return bean;
    }

    public Object getBean(String beanName, Class clazz){
        Iterator<String> it = beanFactories.keySet().iterator();
        Object bean = null;
        while (it.hasNext()) {
            String s = it.next();
            try{
                bean = beanFactories.get(s).getBean(beanName,clazz);
            }catch (Exception e){}
        }
        return bean;
    }

    public Object getBean(String beanName, Object[] args){
        Iterator<String> it = beanFactories.keySet().iterator();
        Object bean = null;
        while (it.hasNext()) {
            String s = it.next();
            try{
                bean = beanFactories.get(s).getBean(beanName, args);
            }catch (Exception e){}
        }
        return bean;
    }

    private void addToMap(BeanFactory beanFactory) {
        String beanFactoryName = beanFactory.getClass().getName() ;
        synchronized(beanFactories){
            if(beanFactories.containsKey(beanFactoryName )){
                throw new IllegalArgumentException("Bean Factory [" + beanFactoryName + "] is already Registered");
            }
            beanFactories.put(beanFactoryName, beanFactory);
        }
    }
    
    
    
}
