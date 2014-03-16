/*
 * ContextPoolTest.java
 *
 * KISS, YAGNI, DRY
 *
 * (c) Copyright 2010-2013, Peter Jakubčo
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package emulib.runtime;

import emulib.annotations.ContextType;
import emulib.emustudio.APITest;
import emulib.plugins.Context;
import emulib.plugins.compiler.CompilerContext;
import emulib.plugins.cpu.CPUContext;
import emulib.plugins.device.DeviceContext;
import emulib.plugins.memory.MemoryContext;
import emulib.runtime.interfaces.PluginConnections;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class ContextPoolTest {
    private CPUContextStub cpuContextMock;
    private MemoryContextStub memContextMock;
    private CompilerContextStub compilerContextMock;
    private DeviceContextStub devContextMock;
    private ContextPool contextPool;
    private final PluginConnections defaultComputer = new ComputerStub(true);

    class ComputerStub implements PluginConnections {
        private final boolean connected;

        public ComputerStub(boolean connected) {
            this.connected = connected;
        }

        @Override
        public boolean isConnected(long pluginID, long toPluginID) {
            return connected;
        }
    }

    @ContextType
    interface DifferentCPUContextStubWithEqualHash extends CPUContextStub {

    }

    @ContextType
    interface DifferentCompilerContextStubWithEqualHash extends CompilerContextStub {

    }

    @ContextType
    interface DifferentMemoryContextStubWithEqualHash extends MemoryContextStub {

    }

    @ContextType
    interface DifferentDeviceContextStubWithEqualHash extends DeviceContextStub {

    }

    @ContextType
    interface FirstEmptyContextStub extends Context {

    }

    @ContextType
    interface SecondEmptyContextStub extends Context {

    }

    @Before
    public void setUp() throws InvalidPasswordException {
        cpuContextMock = EasyMock.createNiceMock(CPUContextStub.class);
        memContextMock = EasyMock.createNiceMock(MemoryContextStub.class);
        compilerContextMock = EasyMock.createNiceMock(CompilerContextStub.class);
        devContextMock = EasyMock.createNiceMock(DeviceContextStub.class);
        replay(cpuContextMock, memContextMock, compilerContextMock, devContextMock);

        contextPool = ContextPool.getInstance();
        APITest.assignEmuStudioPassword();
        assertTrue(contextPool.setComputer(APITest.getEmuStudioPassword(), defaultComputer));
    }

    @After
    public void tearDown() throws InvalidPasswordException {
        contextPool.clearAll(APITest.getEmuStudioPassword());
        verify(cpuContextMock, memContextMock, compilerContextMock, devContextMock);
        contextPool = null;
    }

    @Test
    public void testThatContextIsSingleton() {
        assertNotNull(contextPool);
        assertEquals(contextPool, ContextPool.getInstance());
    }

    @Test
    public void testRegisterCPU() throws InvalidContextException, AlreadyRegisteredException {
        contextPool.register(0, cpuContextMock, CPUContext.class);
        assertEquals(cpuContextMock, contextPool.getCPUContext(0, CPUContext.class));
        assertTrue(contextPool.unregister(0, CPUContext.class));
    }

    @Test
    public void testRegisterTwoTimesCPUAndAccessThemByIndex() throws InvalidContextException, AlreadyRegisteredException {
        CPUContextStub anotherCpuContextMock = EasyMock.createNiceMock(CPUContextStub.class);
        replay(anotherCpuContextMock);
        contextPool.register(0, cpuContextMock, CPUContext.class);
        contextPool.register(0, anotherCpuContextMock, CPUContext.class);

        // Access both contexts
        assertEquals(cpuContextMock, contextPool.getCPUContext(0, CPUContext.class, 0));
        assertEquals(anotherCpuContextMock, contextPool.getCPUContext(0, CPUContext.class, 1));

        // single unregister should remove all contexts for the owner
        assertTrue(contextPool.unregister(0, CPUContext.class));
        assertNull(contextPool.getCPUContext(0, CPUContext.class));
    }

    @Test
    public void testRegisterCPUAccessibleByTwoInterfacesWithEqualHash() throws InvalidContextException, AlreadyRegisteredException {
        contextPool.register(0, cpuContextMock, CPUContextStub.class);
        assertEquals(cpuContextMock, contextPool.getCPUContext(0, CPUContextStub.class));
        assertEquals(cpuContextMock, contextPool.getCPUContext(0, DifferentCPUContextStubWithEqualHash.class));
        assertTrue(contextPool.unregister(0, CPUContextStub.class));
    }

    @Test
    public void testRegisterCompiler() throws InvalidContextException, AlreadyRegisteredException {
        contextPool.register(1, compilerContextMock, CompilerContext.class);
        assertEquals(compilerContextMock, contextPool.getCompilerContext(1, CompilerContext.class));
        assertTrue(contextPool.unregister(1, CompilerContext.class));
    }

    @Test
    public void testRegisterCompilerAccessibleByTwoInterfacesWithEqualHash() throws InvalidContextException, AlreadyRegisteredException {
        contextPool.register(1, compilerContextMock, CompilerContextStub.class);
        assertEquals(compilerContextMock, contextPool.getCompilerContext(1, CompilerContextStub.class));
        assertEquals(compilerContextMock, contextPool.getCompilerContext(1, DifferentCompilerContextStubWithEqualHash.class));
        assertTrue(contextPool.unregister(1, CompilerContextStub.class));
    }

    @Test
    public void testRegisterTwoTimesCompilerAndAccessThemByIndex() throws InvalidContextException, AlreadyRegisteredException {
        CompilerContextStub anotherCompilerContextMock = EasyMock.createNiceMock(CompilerContextStub.class);
        replay(anotherCompilerContextMock);
        contextPool.register(1, compilerContextMock, CompilerContext.class);
        contextPool.register(1, anotherCompilerContextMock, CompilerContext.class);

        // Access both contexts
        assertEquals(compilerContextMock, contextPool.getCompilerContext(1, CompilerContext.class, 0));
        assertEquals(anotherCompilerContextMock, contextPool.getCompilerContext(1, CompilerContext.class, 1));

        // single unregister should remove all contexts for the owner
        assertTrue(contextPool.unregister(1, CompilerContext.class));
        assertNull(contextPool.getCompilerContext(1, CompilerContext.class));
    }

    @Test
    public void testRegisterMemory() throws InvalidContextException, AlreadyRegisteredException {
        contextPool.register(2, memContextMock, MemoryContext.class);
        assertEquals(memContextMock, contextPool.getMemoryContext(2, MemoryContext.class));
        assertTrue(contextPool.unregister(2, MemoryContext.class));
    }

    @Test
    public void testRegisterMemoryAccessibleByTwoInterfacesWithEqualHash() throws InvalidContextException, AlreadyRegisteredException {
        contextPool.register(2, memContextMock, MemoryContextStub.class);
        assertEquals(memContextMock, contextPool.getMemoryContext(2, MemoryContextStub.class));
        assertEquals(memContextMock, contextPool.getMemoryContext(2, DifferentMemoryContextStubWithEqualHash.class));
        assertTrue(contextPool.unregister(2, MemoryContextStub.class));
    }

    @Test
    public void testRegisterTwoTimesMemoryAndAccessThemByIndex() throws InvalidContextException, AlreadyRegisteredException {
        MemoryContextStub anotherMemoryContextMock = EasyMock.createNiceMock(MemoryContextStub.class);
        replay(anotherMemoryContextMock);
        contextPool.register(2, memContextMock, MemoryContext.class);
        contextPool.register(2, anotherMemoryContextMock, MemoryContext.class);

        // Access both contexts
        assertEquals(memContextMock, contextPool.getMemoryContext(2, MemoryContext.class, 0));
        assertEquals(anotherMemoryContextMock, contextPool.getMemoryContext(2, MemoryContext.class, 1));

        // single unregister should remove all contexts for the owner
        assertTrue(contextPool.unregister(2, MemoryContext.class));
        assertNull(contextPool.getMemoryContext(2, MemoryContext.class));
    }

    @Test
    public void testRegisterDevice() throws InvalidContextException, AlreadyRegisteredException {
        contextPool.register(3, devContextMock, DeviceContext.class);
        assertEquals(devContextMock, contextPool.getDeviceContext(3, DeviceContext.class));
        assertTrue(contextPool.unregister(3, DeviceContext.class));
    }

    @Test
    public void testRegisterDeviceAccessibleByTwoInterfacesWithEqualHash() throws InvalidContextException, AlreadyRegisteredException {
        contextPool.register(3, devContextMock, DeviceContextStub.class);
        assertEquals(devContextMock, contextPool.getDeviceContext(3, DeviceContextStub.class));
        assertEquals(devContextMock, contextPool.getDeviceContext(3, DifferentDeviceContextStubWithEqualHash.class));
        assertTrue(contextPool.unregister(3, DeviceContextStub.class));
    }

    @Test
    public void testRegisterTwoContextsWithEqualInterfaces() throws AlreadyRegisteredException, InvalidContextException {
        FirstEmptyContextStub firstEmpty = EasyMock.createNiceMock(FirstEmptyContextStub.class);
        SecondEmptyContextStub secondEmpty = EasyMock.createNiceMock(SecondEmptyContextStub.class);
        replay(firstEmpty, secondEmpty);

        contextPool.register(0, firstEmpty, FirstEmptyContextStub.class);
        contextPool.register(0, secondEmpty, SecondEmptyContextStub.class);
    }

    @Test
    public void testRegisterTwoTimesDeviceAndAccessThemByIndex() throws InvalidContextException, AlreadyRegisteredException {
        DeviceContextStub anotherDeviceContextMock = EasyMock.createNiceMock(DeviceContextStub.class);
        replay(anotherDeviceContextMock);
        contextPool.register(3, devContextMock, DeviceContext.class);
        contextPool.register(3, anotherDeviceContextMock, DeviceContext.class);

        // Access both contexts
        assertEquals(devContextMock, contextPool.getDeviceContext(3, DeviceContext.class, 0));
        assertEquals(anotherDeviceContextMock, contextPool.getDeviceContext(3, DeviceContext.class, 1));

        // single unregister should remove all contexts for the owner
        assertTrue(contextPool.unregister(3, DeviceContext.class));
        assertNull(contextPool.getDeviceContext(3, DeviceContext.class));
    }

    @Test(expected = InvalidContextException.class)
    public void testUnexpectedContextInterface() throws AlreadyRegisteredException, InvalidContextException {
        contextPool.register(1, memContextMock, CPUContext.class);
    }

    @Test(expected = InvalidContextException.class)
    public void testUnannotatedContextInterface() throws AlreadyRegisteredException, InvalidContextException {
        Context unannotatedContext = EasyMock.createNiceMock(UnannotatedContextStub.class);
        contextPool.register(0, unannotatedContext, UnannotatedContextStub.class);
    }

    @Test(expected = AlreadyRegisteredException.class)
    public void testCPUContextAlreadyRegisteredDifferentOwner() throws InvalidContextException, AlreadyRegisteredException {
        contextPool.register(0, cpuContextMock, CPUContext.class);
        contextPool.register(1, cpuContextMock, CPUContext.class);
    }

    @Test(expected = AlreadyRegisteredException.class)
    public void testCPUContextAlreadyRegisteredSameOwner() throws InvalidContextException, AlreadyRegisteredException {
        contextPool.register(0, cpuContextMock, CPUContext.class);
        contextPool.register(0, cpuContextMock, CPUContext.class);
    }

    @Test(expected = AlreadyRegisteredException.class)
    public void testMemoryContextAlreadyRegisteredDifferentOwner() throws InvalidContextException, AlreadyRegisteredException {
        contextPool.register(2, memContextMock, MemoryContext.class);
        contextPool.register(3, memContextMock, MemoryContext.class);
    }

    @Test(expected = AlreadyRegisteredException.class)
    public void testMemoryContextAlreadyRegisteredSameOwner() throws InvalidContextException, AlreadyRegisteredException {
        contextPool.register(2, memContextMock, MemoryContext.class);
        contextPool.register(2, memContextMock, MemoryContext.class);
    }

    @Test(expected = AlreadyRegisteredException.class)
    public void testCompilerContextAlreadyRegisteredDifferentOwner() throws InvalidContextException, AlreadyRegisteredException {
        contextPool.register(1, compilerContextMock, CompilerContext.class);
        contextPool.register(2, compilerContextMock, CompilerContext.class);
    }

    @Test(expected = AlreadyRegisteredException.class)
    public void testCompilerContextAlreadyRegisteredSameOwner() throws InvalidContextException, AlreadyRegisteredException {
        contextPool.register(1, compilerContextMock, CompilerContext.class);
        contextPool.register(1, compilerContextMock, CompilerContext.class);
    }

    @Test(expected = AlreadyRegisteredException.class)
    public void testDeviceContextAlreadyRegisteredDifferentOwner() throws InvalidContextException, AlreadyRegisteredException {
        contextPool.register(3, devContextMock, DeviceContext.class);
        contextPool.register(2, devContextMock, DeviceContext.class);
    }

    @Test(expected = AlreadyRegisteredException.class)
    public void testDeviceContextAlreadyRegisteredSameOwner() throws InvalidContextException, AlreadyRegisteredException {
        contextPool.register(3, devContextMock, DeviceContext.class);
        contextPool.register(3, devContextMock, DeviceContext.class);
    }

    @Test
    public void testCallUnregisterWithoutRegister() throws InvalidContextException {
        assertFalse(contextPool.unregister(0, CPUContextStub.class));
    }

    @Test
    public void testGetContextWhenNoComputerIsSet() throws InvalidPasswordException, InvalidContextException {
        assertTrue(contextPool.setComputer(APITest.getEmuStudioPassword(), null));
        assertNull(contextPool.getCPUContext(0, CPUContextStub.class));
    }

    @Test(expected = InvalidContextException.class)
    public void testGetNullCPUContext() throws InvalidContextException {
        contextPool.getCPUContext(0, null);
    }

    @Test(expected = InvalidContextException.class)
    public void testGetNullCompilerContext() throws InvalidContextException {
        contextPool.getCompilerContext(1, null);
    }

    @Test(expected = InvalidContextException.class)
    public void testGetNullMemoryContext() throws InvalidContextException {
        contextPool.getMemoryContext(2, null);
    }

    @Test(expected = InvalidContextException.class)
    public void testGetNullDeviceContext() throws InvalidContextException {
        contextPool.getDeviceContext(3, null);
    }

    @Test(expected = InvalidContextException.class)
    public void testGetCPUContextWhichIsNotInterface() throws InvalidContextException {
        contextPool.getCPUContext(0, cpuContextMock.getClass());
    }

    @Test(expected = InvalidContextException.class)
    public void testGetCompilerContextWhichIsNotInterface() throws InvalidContextException {
        contextPool.getCompilerContext(0, compilerContextMock.getClass());
    }

    @Test(expected = InvalidContextException.class)
    public void testGetMemoryContextWhichIsNotInterface() throws InvalidContextException {
        contextPool.getMemoryContext(0, memContextMock.getClass());
    }

    @Test(expected = InvalidContextException.class)
    public void testGetDeviceContextWhichIsNotInterface() throws InvalidContextException {
        contextPool.getDeviceContext(0, devContextMock.getClass());
    }

    @Test
    public void testComputerIsNotSetGetCPU() throws InvalidContextException, AlreadyRegisteredException, InvalidPasswordException {
        assertTrue(contextPool.setComputer(APITest.getEmuStudioPassword(), null));
        contextPool.register(0, cpuContextMock, CPUContext.class);
        assertNull(contextPool.getCPUContext(0, CPUContext.class));
    }

    @Test
    public void testUnregisterInvalidContext() throws AlreadyRegisteredException, InvalidContextException {
        contextPool.register(0, cpuContextMock, CPUContext.class);
        try {
            assertEquals(cpuContextMock, contextPool.getCPUContext(0, CPUContext.class));
            assertFalse(contextPool.unregister(0, MemoryContext.class));
        } finally {
            assertTrue(contextPool.unregister(0, CPUContext.class));
        }
    }

    @Test
    public void testGetContextWithEmustudioPassword() throws InvalidPasswordException, AlreadyRegisteredException, InvalidContextException {
        assertTrue(
                contextPool.setComputer(
                        APITest.getEmuStudioPassword(),
                        new ComputerStub(false)
                )
        );
        contextPool.register(0, cpuContextMock, CPUContext.class);
        assertNotNull(
                contextPool.getCPUContext(
                        APITest.getEmuStudioPassword().hashCode(),
                        CPUContext.class
                )
        );
    }

}
