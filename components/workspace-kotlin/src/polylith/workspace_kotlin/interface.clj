(ns polylith.workspace-kotlin.interface
  (:import (org.jetbrains.kotlin.config CompilerConfiguration)
           (org.jetbrains.kotlin.cli.jvm.compiler KotlinCoreEnvironment EnvironmentConfigFiles)
           (org.jetbrains.kotlin.com.intellij.openapi.util Disposer)
           (org.jetbrains.kotlin.com.intellij.openapi Disposable)
           (org.jetbrains.kotlin.com.intellij.psi PsiManager)
           (org.jetbrains.kotlin.com.intellij.openapi.project Project)
           (org.jetbrains.kotlin.com.intellij.psi PsiFile)
           (org.jetbrains.kotlin.com.intellij.testFramework LightVirtualFile)
           (org.jetbrains.kotlin.idea KotlinFileType)))

;; https://jitinsharma.in/posts/parsing-kotlin-using-code-kotlin/

(def env-config EnvironmentConfigFiles/JVM_CONFIG_FILES)

(def disposer (Disposer/newDisposable))
(def compiler-config (CompilerConfiguration.))

(def env (KotlinCoreEnvironment/createForProduction
           ^Disposable disposer
           ^CompilerConfiguration compiler-config
           ^EnvironmentConfigFiles env-config))

(def project (.getProject env))

(def psi-manager (PsiManager/getInstance project))


(def filename "../kotlin-polylith/components/user/src/interface.kt")


(def file-content "")

(def virtual-file (LightVirtualFile. filename file-content))


(def kt-file (.findFile psi-manager virtual-file))

(str (.getFileType ^PsiFile kt-file))



(.getImportList kt-file)

org.jetbrains.kotlin.psi.KtFilet