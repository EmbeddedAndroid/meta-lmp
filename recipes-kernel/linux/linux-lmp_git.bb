LINUX_VERSION ?= "4.18.18"

FIO_LMP_GIT_URL ?= "github.com"
FIO_LMP_GIT_NAMESPACE ?= "foundriesio/"

SRCREV_machine = "6a5127911565aa3bfde3716ca4cef31ff122a1c8"
SRCREV_meta = "aa3f5f6f9e4e52b34a01f440ecd6a279be3895eb"
KBRANCH = "linux-v4.18.y"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

SRC_URI = "git://${FIO_LMP_GIT_URL}/${FIO_LMP_GIT_NAMESPACE}linux.git;protocol=https;branch=${KBRANCH};name=machine; \
    git://github.com/EmbeddedAndroid/lmp-kernel-cache.git;protocol=https;type=kmeta;name=meta;branch=master;destsuffix=${KMETA} \
"

KMETA = "kernel-meta"

require linux-lmp.inc
require linux-lmp-machine-custom.inc
