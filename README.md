 We'll use Fabric for Minecraft Java Edition 1.21, and the project will be developed in VS Code. Here's a basic file structure for your mod:

```
your_mod_name/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── yourusername/
│       │           └── yourmodname/
│       │               ├── YourModName.java
│       │               ├── entity/
│       │               │   ├── CustomNpcEntity.java
│       │               │   └── CustomNpcRenderer.java
│       │               ├── client/
│       │               │   └── YourModNameClient.java
│       │               └── mixin/
│       │                   └── ExampleMixin.java
│       └── resources/
│           ├── assets/
│           │   └── yourmodname/
│           │       ├── lang/
│           │       │   └── en_us.json
│           │       ├── textures/
│           │       │   └── entity/
│           │       │       └── custom_npc.png
│           │       └── models/
│           │           └── entity/
│           │               └── custom_npc.json
│           ├── data/
│           │   └── yourmodname/
│           │       └── tags/
│           │           └── entity_types/
│           │               └── custom_npc.json
│           └── fabric.mod.json
│
├── gradle/
├── .gitignore
├── build.gradle
├── gradle.properties
└── settings.gradle
```

Now, let's go through the main components:

1. `YourModName.java`: This is the main mod class where you'll initialize your mod.

2. `CustomNpcEntity.java`: This class will define the behavior of your NPC, including following the specific player.

3. `CustomNpcRenderer.java`: This class will handle rendering the NPC.

4. `YourModNameClient.java`: This class will handle client-side initialization, such as registering the entity renderer.

5. `ExampleMixin.java`: This is a placeholder for any mixins you might need (you may not need this for this specific mod).

6. `en_us.json`: This file will contain language translations for your mod.

7. `custom_npc.png`: This will be the texture for your NPC.

8. `custom_npc.json`: This file will define the model for your NPC.

9. `fabric.mod.json`: This is the mod metadata file required by Fabric.

10. `build.gradle`, `gradle.properties`, and `settings.gradle`: These files are used for configuring your project build.

To implement the NPC that follows a specific player, you'll primarily work with the `CustomNpcEntity.java` file. You'll need to override the AI goals to make the NPC follow the specified player.

For tracking a specific player, you can use either their UUID or username. Using UUID is generally more reliable as it doesn't change, while usernames can be changed. You can store the target player's UUID in the entity's NBT data and use it to find the player in the world.

What We Need to Implement:

1. Custom AI Goal: We'll need to create a custom AI goal for our NPC to follow a specific player. This will extend one of Minecraft's existing goal classes, likely `Goal` or `TargetGoal`.

2. Player Targeting: We'll implement logic to identify and target the specific player based on their UUID or username.

3. Goal Priority: We'll need to set up the priority of our custom follow goal in relation to other goals the NPC might have.

Here's a basic outline of how we might implement this:

```java
public class FollowSpecificPlayerGoal extends Goal {
    private final CustomNpcEntity npc;
    private final UUID targetPlayerUUID;
    private Player targetPlayer;
    private final double speedModifier;
    private final PathNavigation navigation;

    public FollowSpecificPlayerGoal(CustomNpcEntity npc, UUID targetPlayerUUID, double speedModifier) {
        this.npc = npc;
        this.targetPlayerUUID = targetPlayerUUID;
        this.speedModifier = speedModifier;
        this.navigation = npc.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.targetPlayer == null || !this.targetPlayer.isAlive()) {
            this.targetPlayer = this.npc.level().getPlayerByUUID(this.targetPlayerUUID);
        }
        return this.targetPlayer != null && this.targetPlayer.isAlive();
    }

    @Override
    public void start() {
        this.navigation.moveTo(this.targetPlayer, this.speedModifier);
    }

    @Override
    public void tick() {
        this.npc.getLookControl().setLookAt(this.targetPlayer, 10.0F, (float)this.npc.getMaxHeadXRot());
        if (!this.npc.isPathFinding()) {
            this.navigation.moveTo(this.targetPlayer, this.speedModifier);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse() && !this.navigation.isDone();
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        this.navigation.stop();
    }
}
```

In the `CustomNpcEntity` class, we would add this goal to the NPC's goal selector:

```java
protected void registerGoals() {
    this.goalSelector.addGoal(1, new FollowSpecificPlayerGoal(this, targetPlayerUUID, 1.0D));
    // Add other goals as needed
}

1. Using the Player Model:
   Minecraft already has a `PlayerModel` class that we can use for our NPC. This model includes all the standard player features like arms, legs, head, and even supports different arm poses.

2. Custom Skin:
   We can create a custom skin for our NPC, just like player skins. This will be a PNG file with the standard Minecraft skin layout.

Here's how we can implement this:

1. First, create your custom skin PNG file and place it in your mod's resources:
   `src/main/resources/assets/yourmodname/textures/entity/custom_npc_skin.png`

2. Create a renderer for your NPC that uses the player model:

```java
public class CustomNpcRenderer extends EntityRenderer<CustomNpcEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("yourmodname", "textures/entity/custom_npc_skin.png");
    private final PlayerModel<CustomNpcEntity> model;

    public CustomNpcRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false);
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
    }

    @Override
    public void render(CustomNpcEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        matrixStack.pushPose();
        this.model.young = false;
        this.model.setAllVisible(true);
        this.model.setupAnim(entity, 0, 0, entity.tickCount + partialTicks, entity.getYRot(), entity.getXRot());
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
        this.model.renderToBuffer(matrixStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CustomNpcEntity entity) {
        return TEXTURE;
    }
}
```

3. Register the renderer in your client initialization:

```java
@Environment(EnvType.CLIENT)
public class YourModNameClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(YourModName.CUSTOM_NPC, CustomNpcRenderer::new);
    }
}
```

4. In your main mod class, register the entity:

```java
public class YourModName implements ModInitializer {
    public static final EntityType<CustomNpcEntity> CUSTOM_NPC = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier("yourmodname", "custom_npc"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CustomNpcEntity::new)
            .dimensions(EntityDimensions.fixed(0.6F, 1.8F)) // Standard player dimensions
            .build()
    );

    @Override
    public void onInitialize() {
        // Other initialization code...
    }
}
```

