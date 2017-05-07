package sirius.core;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.W32APIOptions;

public interface Psapi extends WinNT {
    public static final Psapi INSTANCE = (Psapi)Native.loadLibrary("Psapi.dll", Psapi.class, W32APIOptions.DEFAULT_OPTIONS);
    public abstract WinDef.DWORD GetModuleFileNameExW(WinNT.HANDLE hProcess, WinNT.HMODULE hModule, char[] pathName, WinNT.DWORD pathNameSize);
}