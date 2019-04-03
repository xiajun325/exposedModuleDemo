package cn.manmade.xposeddemo

import android.app.Application
import android.content.Context
import android.util.Log

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Main : IXposedHookLoadPackage, IXposedHookZygoteInit {
    private val TAG: String? = "manmade-hook-lpds"
    private val LUPINGDASHI_PKG_NAME: String = "com.screeclibinvoke"
    private val VIP_HOOK_CLASS_NAME: String = "com.screeclibinvoke.component.manager.VipManager"
    private val VIP_HOOK_FUN_NAME: String = "isLevel3"

    @Throws(Throwable::class)
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        Log.w(TAG, "initZygote" + startupParam!!.modulePath)
    }

    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {


        //Log.w(TAG,"handleLoadPackage_"+lpparam!!.packageName+",progressName:"+lpparam!!.processName)
        when (lpparam!!.packageName) {
            LUPINGDASHI_PKG_NAME ->
                XposedHelpers.findAndHookMethod(Application::class.java, "attach", Context::class.java, object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        val cl: ClassLoader = (param!!.args[0] as Context).getClassLoader();
                        var hookclass: Class<*>?;
                        try {
                            hookclass = cl.loadClass(VIP_HOOK_CLASS_NAME);
                        } catch (e: Exception) {
                            Log.e(TAG, "寻找xxx.xxx.xxx报错", e);
                            return;
                        }

                        XposedHelpers.findAndHookMethod(hookclass, VIP_HOOK_FUN_NAME, object : XC_MethodHook() {

                            @Throws(Throwable::class)
                            override fun afterHookedMethod(param: MethodHookParam?) {
                                super.afterHookedMethod(param)
                                param!!.result = true;
                                //          param!!.args[0] = "_" as CharSequence
                            }

//                            @Throws(Throwable::class)
//                            override fun beforeHookedMethod(param: MethodHookParam?) {
//                                super.beforeHookedMethod(param)
//                                Log.w(TAG, "setText:" + param!!.args[0] + " replace " + "_");
//                                param!!.args[0] = "_" as CharSequence
//                            }

                        });
                    }
                });
            else -> return

        }
    }

}