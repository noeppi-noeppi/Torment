// Sometimes mixin is annoying. Just did a coremod for this one.

function transformMethod(method) {
    var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
    var Opcodes = Java.type('org.objectweb.asm.Opcodes');
    var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
    var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
    var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
    var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
    var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

    var target = new InsnList();
    target.add(new VarInsnNode(Opcodes.ALOAD, 1));
    target.add(ASMAPI.buildMethodCall(
        'io/github/noeppi_noeppi/mods/torment/effect/instances/MobSwapEffect',
        'transformRenderEntity', '(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/entity/Entity;',
        ASMAPI.MethodType.STATIC
    ));
    target.add(new VarInsnNode(Opcodes.ASTORE, 1))

    method.instructions.insert(target);

    return method;
}

function initializeCoreMod() {
    return {
        'should_render': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.entity.EntityRenderDispatcher',
                'methodName': 'm_114397_',
                'methodDesc': '(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z'
            },
            'transformer': transformMethod
        },
        'render': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.entity.EntityRenderDispatcher',
                'methodName': 'm_114384_',
                'methodDesc': '(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V'
            },
            'transformer': transformMethod
        }
    }
}

