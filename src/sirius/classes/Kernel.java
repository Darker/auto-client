 package sirius.classes;
 
 import com.sun.jna.Structure;
 import com.sun.jna.platform.win32.WinDef;
 import sirius.constants.IMEMConsts;
 
 public class Kernel
   extends Common
   implements IMEMConsts
 {
   public void VirtualAllocateMemory(Structure struct)
   {
     getKernel32().VirtualAllocEx(getKernel32().GetCurrentProcess(), struct.getPointer(), struct.size(), new WinDef.DWORD(4096L), new WinDef.DWORD(536870912L));
   }
   
   public void VirtualFreeMemory(Structure struct)
   {
     getKernel32().VirtualFreeEx(getKernel32().GetCurrentProcess(), struct.getPointer(), struct.size(), new WinDef.DWORD(16384L));
   }
 }

