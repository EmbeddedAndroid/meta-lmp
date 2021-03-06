SUMMARY = "OP-TEE Trusted OS"
HOMEPAGE = "https://github.com/foundriesio/optee_os.git"
DESCRIPTION = "OPTEE OS"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=c1f21c4f72f372ef38a5a4aee55ec173"

DEPENDS = "python3-pycryptodome-native python3-pycryptodomex-native python3-pyelftools-native"

SRC_URI = "git://github.com/foundriesio/optee_os.git;branch=${SRCBRANCH}"

PV = "3.10.0+git"
SRCREV = "ba1333a53696937b661c966c457bc4e4a04ebcd8"
SRCBRANCH = "3.10+fio"

# To be removed once it can fully replace the 3.6.0 based version
DEFAULT_PREFERENCE = "-1"

S = "${WORKDIR}/git"

inherit deploy python3native

PROVIDES = "virtual/optee-os"

OPTEEMACHINE ?= "${MACHINE}"

# TA Signing Key, can be set to replace the default RSA 2048 key (default_key.pem)
OPTEE_TA_SIGN_KEY ?= ""

PACKAGE_ARCH = "${MACHINE_ARCH}"
OPTEE_ARCH_armv7a = "arm32"
OPTEE_ARCH_armv7ve = "arm32"
OPTEE_ARCH_aarch64 = "arm64"

EXTRA_OEMAKE = "PLATFORM=${OPTEEMACHINE} O=out/arm \
                CROSS_COMPILE_core=${HOST_PREFIX} \
                CFG_WERROR=y DEBUG=0 LDFLAGS= \
                LIBGCC_LOCATE_CFLAGS=--sysroot=${STAGING_DIR_HOST} \
                CFG_TEE_CORE_LOG_LEVEL=2 CFG_TEE_TA_LOG_LEVEL=2 \
"
EXTRA_OEMAKE += "${@oe.utils.ifelse('${OPTEE_TA_SIGN_KEY}' != '', 'TA_SIGN_KEY=${OPTEE_TA_SIGN_KEY}', '')}"

EXTRA_OEMAKE_append_aarch64 = " \
                CFG_ARM64_core=y \
                CROSS_COMPILE_ta_arm64=${HOST_PREFIX} \
                ta-targets=ta_arm64 \
"
EXTRA_OEMAKE_append_armv7a = " \
                CROSS_COMPILE_ta_arm32=${HOST_PREFIX} \
                ta-targets=ta_arm32 \
"
EXTRA_OEMAKE_append_armv7ve = " \
                CROSS_COMPILE_ta_arm32=${HOST_PREFIX} \
                ta-targets=ta_arm32 \
"

do_install() {
    # TA devkit
    install -d ${D}${includedir}/optee/export-user_ta/
    for f in ${B}/out/arm/export-ta_${OPTEE_ARCH}/* ; do
        cp -aR $f ${D}${includedir}/optee/export-user_ta/
    done

    # OP-TEE OS firmware
    install -d ${D}/${nonarch_base_libdir}/firmware
    install -m 644 ${B}/out/arm/core/*.bin ${D}/${nonarch_base_libdir}/firmware/

    # OP-TEE OS TAs
    install -d ${D}${nonarch_base_libdir}/optee_armtz
    install -m 0444 ${S}/out/arm/ta/*/*.ta ${D}${nonarch_base_libdir}/optee_armtz
}

do_deploy() {
    install -d ${DEPLOYDIR}/optee
    for f in ${B}/out/arm/core/*.bin; do
        install -m 644 $f ${DEPLOYDIR}/optee/
    done
    # Link tee.bin so it can be consumed by recipes such as imx-boot
    ln -sf optee/tee-pager_v2.bin ${DEPLOYDIR}/tee.bin
    readelf -h ${B}/out/arm/core/tee.elf | grep Entry | awk '{print $4}' > ${B}/out/arm/core/tee-init_load_addr.txt
    install -m 644 ${B}/out/arm/core/tee-init_load_addr.txt ${DEPLOYDIR}/optee/
}

addtask deploy before do_build after do_install

PACKAGES += "${PN}-ta-pkcs11 ${PN}-ta"
FILES_${PN} += "${nonarch_base_libdir}/firmware"
FILES_${PN}-ta = "${nonarch_base_libdir}/optee_armtz"
FILES_${PN}-ta-pkcs11 = "${nonarch_base_libdir}/optee_armtz/fd02c9da-306c-48c7-a49c-bbd827ae86ee.ta"
FILES_${PN}-dev = "${includedir}/optee"
INSANE_SKIP_${PN}-dev = "staticdev"
INHIBIT_PACKAGE_STRIP = "1"
