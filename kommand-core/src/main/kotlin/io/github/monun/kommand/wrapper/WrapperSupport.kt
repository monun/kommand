package io.github.monun.kommand.wrapper

import io.github.monun.kommand.loader.LibraryLoader

interface WrapperSupport {
    companion object : WrapperSupport by LibraryLoader.load(WrapperSupport::class.java)

    fun entityAnchorFeet(): EntityAnchor

    fun entityAnchorEyes(): EntityAnchor
}