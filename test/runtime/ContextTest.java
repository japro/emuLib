/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package runtime;

import java.lang.reflect.Type;
import plugins.cpu.ICPUContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vbmacher
 */
public class ContextTest {

    public interface ITestContext extends ICPUContext {
        public void testMethod();
    }

    public class TestContext implements ITestContext {

        @Override
        public void testMethod() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void addCPUListener(ICPUListener listener) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removeCPUListener(ICPUListener listener) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getHash() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    public ContextTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testInterfaces() {
        TestContext context = new TestContext();
        
        Class<?>[] intf = context.getClass().getInterfaces();
        for (int i = 0; i < intf.length; i++) {
            System.out.print(intf[i].toString());
            System.out.println(" instanceof ICPUContext: " + (context instanceof ICPUContext));
            System.out.println("(" + intf[i].getSimpleName()  +  ")");
        }
    }

}