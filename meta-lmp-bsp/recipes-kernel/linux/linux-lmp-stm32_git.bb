LINUX_VERSION ?= "5.4.56"

FIO_LMP_GIT_URL ?= "github.com"
FIO_LMP_GIT_NAMESPACE ?= "foundriesio/"

SRCREV_machine = "ec918908fd55be1ce4cd1357ca68f9bf7174b81a"
SRCREV_meta = "fa79e445bb60a149be0892f60e6aff5504ef3e8e"
KBRANCH_machine = "v5.4-stm32mp"
KBRANCH_meta = "linux-v5.4.y"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

SRC_URI = "git://github.com/STMicroelectronics/linux.git;protocol=https;branch=${KBRANCH_machine};name=machine; \
    git://${FIO_LMP_GIT_URL}/EmbeddedAndroid/lmp-kernel-cache.git;protocol=https;type=kmeta;name=meta;branch=${KBRANCH_meta};destsuffix=${KMETA} \
"

KMETA = "kernel-meta"

include recipes-kernel/linux/linux-lmp.inc
