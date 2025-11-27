# Busque Conhecimento

## 1. Identificação

Nome: Júlio Augusto de Barros Mansan <br>
Curso: Sistemas de Informação.


## 2. Objetivo

Criar uma experiência gamificada baseada em perguntas de múltipla escolha relacionadas a diferentes áreas do conhecimento. Nesta experiência, o usuário deve escolher um personagem dentre quatro disponíveis. Cada personagem possui perguntas de alguma área específica, com base em suas características. O jogador deve passar por 3 mapas, derrotando inimigos comuns com perguntas fáceis e chegando a um inimigo mais forte, onde terá de responder corretamente a 3 perguntas de nível difícil. 

O usuário tem 3 vidas. Caso responda 1 questão incorretamente em qualquer parte do mapa, ele perde 1 vida. Caso perca as 3, o jogo reinicia. A cada troca de mapa, a vida regenera.

## 3. Desenvolvimento

### MainMenuScreen

Para iniciar o projeto, pensei em um objetivo extremamente simples: criar uma imagem que preenchese toda a janela do aplicativo. Parecia uma tarefa superficial no começo, porém foi essencial para a compreensão de alguns conceitos essenciais para a criação de jogos e afins, como a utilização do ``SpriteBatch``, ``TextureAtlas`` e ``Viewport``. Este último se mostrou um tanto complexo, mas passível de entendimento após um tempo.

Percebi que o jogo teria uma quantidade significativa de telas e que todas possuíriam alguns atributos e métodos em comum, por isso, criei uma classe-mãe chamada ``GameScreen``, responsável por renderizar as telas e ajustar o ``Viewport``.

````java
public GameScreen(MainGame game, String atlasPath, String regionName) {
        this.game = game;
        this.batch = new SpriteBatch();

        // Carrega o atlas e a região usada como background
        this.atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
        this.background = atlas.findRegion(regionName);

        // Viewport que estica a imagem para caber no tamanho da janela
        this.viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT);
        // Centraliza a câmera do viewport
        this.viewport.getCamera().position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);

        // Stage usado para UI, com viewport que mantém proporção
        this.stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
}
````

Consegui colocar uma imagem placeholder que preenchia toda a tela, independente de seu tamanho. Concluído esse objetivo, a próxima tarefa era criar um botão para trocar entre telas. Nesse contexto, tomei conhecimento de dois aspectos imprescendíveis: ``Stage`` e ``Table``. Este facilita o posicionamento de determinados layouts na tela, e aquele gerencia os inputs do usuário. 

Percebi que o jogo demandaria certa quantia de botões, então criei uma classe ``ButtonGame``, que recebe um texto como parâmetro e cria um objeto ``TextButton`` com isso. Devido à semelhança de estilo que os botões possuem, o construtor da classe se utiliza de um design padrão fornecido em um ``TextureAtlas`` dentro da pasta assets.

````java
public ButtonGame(String text, float fontScale) {
        // Carrega atlas com as imagens do botão (Pressed / Unpressed)
        this.atlas = new TextureAtlas("assets/assets.atlas");

        // Skin que gerencia os drawables do atlas
        this.skin = new Skin();
        skin.addRegions(atlas);

        // Criação do estilo do botão
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = skin.getDrawable("Button_Unpressed"); // Estado normal
        style.down = skin.getDrawable("Button_Pressed"); // Estado pressionado

        // Fonte usada no texto do botão
        font = new BitmapFont(Gdx.files.internal("fonts/hud.fnt"));
        if (fontScale != 1f) {
            font.getData().setScale(fontScale); // Ajusta o tamanho da fonte, se necessário
        }
        style.font = font;

        // Cria o botão com o estilo definido
        button = new TextButton(text, style);

        // Permite que o texto quebre linha quando necessário
        button.getLabel().setWrap(true);
}

````

### SelectScreen

Finalizada, por ora, a tela de Menu Principal, progredi para a criação da tela de seleção de personagens. Optei por manter a mesma imagem da tela anterior, apenas como placeholder. O próximo passo era criar quatro botões representando os personagens disponíveis para a escolha do jogador.

Não se mostrou uma tarefa árdua, pois a utilização de ``Table`` auxiliou bastante no processo. Pude assim, organizar os quatro inputs na parte inferior da tela.

````java
public SelectScreen(MainGame game) {
        // Carrega o fundo padrão usando o construtor da classe pai
        super(game, "assets/assets.atlas", "Background_Select");

        // Define o Stage como processador de entrada
        Gdx.input.setInputProcessor(stage);

        // Mundo físico com gravidade
        this.world = new World(new Vector2(0, -10), true);

        // Inicializa listas
        this.characterButtons = new ArrayList<ButtonGame>();
        this.characters = new ArrayList<Character>();

        // Cria um botão para cada personagem
        for (String name : characterNames) {
            ButtonGame button = new ButtonGame(name);
            characterButtons.add(button);
        }

        // Tabela para organizar os botões na parte inferior da tela
        Table table = new Table();
        table.setFillParent(true);
        table.center().bottom();
        table.padBottom(100);

        // Espaço horizontal entre personagens
        float spacing = WORLD_WIDTH / (characterNames.length);

        // Adiciona botões na tabela e cria personagens animados acima deles
        for (int i = 0; i < characterButtons.size(); i++) {
            table.add(characterButtons.get(i).getButton()).expandX().size(360, 120);

            // Posição X calculada para distribuir uniformemente
            float x = spacing * (i + 0.5f);

            // Cria o personagem correspondente para exibir sua animação
            characters.add(new Character(world, "characters/" + characterNames[i] + ".atlas", x, 46f, 0.1f));
        }

        // Adiciona a tabela ao Stage
        stage.addActor(table);

        // Adiciona listeners para cada botão
        for (int i = 0; i < characterButtons.size(); i++) {
            final int index = i;

            characterButtons.get(i).getButton().addListener(new ClickListener() {
                String name = characterNames[index]; // Nome do personagem selecionado

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Ao clicar, troca para a MainScreen já com o personagem escolhido
                    game.setScreen(new MainScreen(game, name));
                    dispose();
                }
            });
        }
}

````

### MainScreen

A próxima etapa do projeto era toda a lógica do jogo em si. Criei então uma ``MainScreen``, que demandava quatro tarefas em paralelo:

- Renderizar um mapa;
- Criar o jogador;
- Criar o inimigo;
- Buscar uma questão e mostrá-la na tela.

Para renderizar o mapa, foi necessário gerar um ``TiledMap`` e adequar o ``Viewport``, a fim de que se encaixasse perfeitamente na janela. Acabei enfrentando alguns problemas quanto a isso. Por vezes, o mapa ficava descentralizado ou nem carregava, mas depois de alguns ajustes, pude carregá-lo sem maiores problemas. A propósito, utilizei o software Tiled para criar os mapas.

Quanto à criação do jogador e do inimigo, cogitei criar duas classes diferentes para ambos. Porém, como eles compartilham os mesmos atributos e métodos, gerei uma classe ``Character``, apenas passando para seu construtor os parâmetros necessários.

````java
public Character(World world, int health, String atlas, Float posX, Float posY, Float scale) {
        this.world = world;

        // Cria corpo Box2D do personagem
        defineCharacter(posX, posY);

        this.health = health;
        this.atlas = new TextureAtlas(atlas);
        this.scale = scale;

        // Valores iniciais de suavização
        this.smoothX = posX;
        this.smoothY = posY;

        // Carrega animações do atlas
        this.idleAnimation = new Animation<>(0.25f, this.atlas.findRegions("Idle"), Animation.PlayMode.LOOP);
        this.attackAnimation = new Animation<>(0.15f, this.atlas.findRegions("Attack"), Animation.PlayMode.NORMAL);
        this.hurtAnimation = new Animation<>(0.25f, this.atlas.findRegions("Hurt"), Animation.PlayMode.NORMAL);

        this.currentState = State.Idle; // Começa parado
        this.stateTimer = 0; // Reseta timer de animação

        // Primeiro frame
        this.characterRegion = idleAnimation.getKeyFrame(stateTimer);
        setRegion(characterRegion);
}
````

A parte das questões gerou algumas dúvidas. De início, pensei em consumir dados de uma API (https://opentdb.com/api_config.php), porém os dados se encontravam na língua inglesa e pretendia manter o jogo traduzido. Então, fiz algumas chamadas, traduzindo as questões e adaptando para um estilo diferente em alguns arquivos ``.json`` ao longo do programa. Para melhor controle, criei uma classe ``Question``, que se utilizava de uma Label para enunciado, e quatro ButtonGames para as respostas. 

````java
public Question(String[] answerOptions, Viewport viewport, Stage stage, int correctIndex, String text) {
        // Carrega fonte usada no texto da pergunta
        this.questionFont = new BitmapFont(Gdx.files.internal("fonts/hud.fnt"));
        this.questionFont.getData().setScale(0.6f);

        this.optionsButtons = new ButtonGame[4]; // Sempre 4 alternativas
        this.correctIndex = correctIndex;
        this.text = text;
        this.viewport = viewport;
        this.stage = stage;

        // Carrega a textura de fundo da caixa da pergunta
        Texture texture = new Texture(Gdx.files.internal("assets/background.png"));
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(texture));

        // Estilo do texto da pergunta
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = questionFont;
        style.background = background;

        // Label com o texto da pergunta
        this.questionLabel = new Label(text, style);
        questionLabel.setAlignment(Align.center);
        questionLabel.setWrap(true); // Permite quebra de linha automática

        // Tabela que organiza a pergunta e as opções na tela
        this.table = new Table();
        table.setFillParent(true); // Preenche a tela inteira
        table.center().top(); // Posição no topo
        table.add(questionLabel).colspan(2).width(500).padBottom(20);
        table.row();

        // Cria e adiciona os botões das opções
        for (int i = 0; i < 4; i++) {
            optionsButtons[i] = new ButtonGame((i + 1) + ") " + answerOptions[i], 0.5f);
            table.add(optionsButtons[i].getButton()).width(300).height(50).pad(10);

            // A cada duas opções, pula para linha de baixo
            if (i % 2 == 1) {
                table.row();
            }
        }

        // Adiciona na Stage se existir
        if (stage != null) {
            stage.addActor(table);
        }
}
````

Com isso concluído, o esqueleto do projeto estava pronto. Entretanto, duas coisas me incomodavam: a grande quantidade de linhas que ``MainScreen`` possuía e o fato dos personagens estarem estáticos. 

````java
public MainScreen(MainGame game, String characterName) {
        // Usa construtor básico do GameScreen (sem background automático)
        super(game);

        // Camera ortográfica que será usada no gameplay
        this.camera = new OrthographicCamera();

        // Substitui o viewport padrão por outro reduzido pela metade
        super.viewport = new StretchViewport(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, camera);

        // Carrega o mapa específico do personagem
        this.map = new Map(characterName, camera);

        // Mundo Box2D com gravidade
        this.world = new World(new Vector2(0, -10), true);

        // Cria o personagem do jogador
        this.player = new Character(world, 3,
                "characters/" + characterName + ".atlas",
                320f, 46f, 0.35f);

        // Cria o inimigo inicial da fase
        this.enemy = new Character(world, 1,
                "enemies/" + characterName + "/enemy1.atlas",
                enemyWidth, 55f, 0.3f);

        // Novo Stage usando o viewport redefinido
        this.stage = new Stage(super.viewport);
        Gdx.input.setInputProcessor(stage);

        // Carrega atlas com corações (vidas)
        this.atlas = new TextureAtlas("assets/hearts.atlas");

        // Skin usada para armazenar regiões do atlas
        this.skin = new Skin();
        skin.addRegions(atlas);

        // Textura das vidas
        this.heartTexture = skin.getRegion("3hearts");

        // Cria imagem do coração e aumenta o tamanho
        Image heartImage = new Image(heartTexture);
        heartImage.setScale(1.5f);

        // Gerenciador que controla as fases, as perguntas e o combate
        this.phaseManager = new PhaseManager(
                player, enemy, world, enemyWidth,
                viewport, stage, skin, characterName, map);

        // Obtém a tabela da pergunta atual e coloca o coração nela
        Table table = phaseManager.getCurrentQuestion().getTable();
        table.add(heartImage).padTop(50);

        // Liga o listener da lógica de fase, controlando o fluxo das perguntas
        phaseManager.addListener(heartImage, table, game, this);

        // Adiciona a tabela renderizada à Stage
        stage.addActor(table);

        // Se já estiver na última fase (fase 3), retorna automaticamente ao menu
        if (phaseManager.getCurrentPhase() == 3) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
}
````

Para resolver o primeiro problema, criei uma classe ``PhaseManager``, que seria responsável pela lógica principal do jogo: tratar respostas das questões, controlar animações dos personagens, controlar o fluxo do jogo, etc. A propósito, criei uma classe ``Map`` para controlar o fluxo de troca entre eles e excluir algumas linhas de PhaseManager.

````java
public PhaseManager(Character player, Character enemy, World world, Float enemyWidth, Viewport viewport,
            Stage stage, Skin skin, String character, Map map) {

        this.player = player;
        this.enemy = enemy;
        this.world = world;
        this.enemyWidth = enemyWidth;
        this.isBossLevel = false;
        this.currentIndex = 0;
        this.currentPhase = 1;
        this.skin = skin;
        this.characterName = character;
        this.map = map;

        this.heartTexture = this.skin.getRegion("3hearts"); // Player sempre começa com 3 vidas
        this.enemyQuestions = new ArrayList<>();
        this.bossQuestions = new ArrayList<>();

        // Carrega o arquivo JSON da fase inicial
        loadJson("questions/" + characterName + "_questions.json");

        // Escolhe pergunta aleatória que ainda não foi usada
        int random = randomNum(enemyQuestions);

        // Cria a pergunta na tela
        currentQuestion = new Question(questions[random].options, viewport, stage,
                questions[random].correctIndex, questions[random].text);
}
````

O segundo problema demandou certo esforço. Foi necessário compreender um pouco da classe ``Animation`` da libGdx e controlar as animações a partir de uma mecânica de estado em que o personagem se encontra. Depois de alguns ajustes, as animações de "Idle", "Attack" e "Hurt" estavam funcionando.

### Ajustes

Com a lógica de jogo pronta, comecei a buscar os sprites necessários para inimigos e para os personagens jogáveis. Além de criar todos os 12 mapas necessários. Foi uma tarefa que exigiu bastante tempo, por incrível que pareça. Muitos dos sprites possuíam tamanhos distintos, o que exigiu um pequeno ajuste de escala na classe Character. No fim, optei por deixar inimigos comuns com uma escala menor e "chefões" com escala maior. Entretanto, alguns ainda ocupam espaço em demasia na tela. 

Depois disso, gerei todas as questões necessárias com chamadas frequentes da API e as adaptei para o padrão já estabelecido dentro dos arquivos ``.json``.

Alterei ainda os backgrounds das telas iniciais, substituindo o placeholder e adicionei os quatro personagens na tela de seleção, para que o usuário pudesse ver com quem iria jogar.

Além disso, para deixar as transições mais suaves, apliquei um fade-in/fade-out nas questões passando um parâmetro ``Runnable``. Também apliquei um leve slide para que a entrada dos personagens não fosse tão abrupta. Ademais, apliquei um leve delay para encaixar a animação de ataque com a animação de levar dano, tanto para o inimigo, quanto para o jogador. Por fim, bloqueei o input enquanto alguma animação estiver sendo executada.

### FinishScreen

Uma vez que os ajustes estavam prontos, decidi criar uma tela sinalizando que o jogo encerrou, levando em consideração se o jogador pôde derrotar todos os chefes, ou perdeu todas as vidas no processo. Foi algo bem simples: criei uma classe ``FinishScreen``, classe filha de ``GameScreen`` e seu construtor recebe como parâmetro "Você Venceu!" ou "Você Perdeu!". Assim, a Label escreve tal mensagem na tela e é criado um ``GameButton`` que retorna ao menu principal.

````java
public FinishScreen(String text, MainGame game) {
        // Chama o construtor da classe GameScreen,
        // configura o atlas e o background dessa tela.
        super(game, "assets/assets.atlas", "Background_Select");

        // Cria um botão para retornar ao menu.
        this.returnGame = new ButtonGame("Retornar ao menu", 1.5f);

        // Carrega a fonte usada para exibir a mensagem.
        this.font = new BitmapFont(Gdx.files.internal("fonts/hud.fnt"));
        this.font.getData().setScale(5f); // Aumenta o tamanho da fonte

        // Define que o input atual será tratado pelo Stage desta tela.
        Gdx.input.setInputProcessor(stage);

        // Cria estilo de texto para o Label.
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;

        // Label que exibe o texto de finalização (ex: "Você venceu!" ou "Fim de jogo").
        message = new Label(text, style);
        message.setAlignment(Align.center);

        // Usamos uma Table para organizar o layout na tela.
        Table table = new Table();
        table.setFillParent(true); // Faz a table ocupar toda a tela
        table.center(); // Centraliza todo o conteúdo

        // Adiciona o texto
        table.add(message);
        table.row(); // Pula para a próxima linha da table

        // Adiciona o botão com tamanho e espaçamento configurados
        table.add(returnGame.getButton()).size(560, 200).padTop(5f);

        // Listener do botão: quando clicado, volta para o MainMenuScreen.
        returnGame.getButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game)); // troca de tela
                dispose(); // libera recursos dessa tela
            }
        });

        // Adiciona a Table ao Stage
        stage.addActor(table);
}
````

### HelpScreen

Foi um processo realmente simples, o que me garantiu tempo para criar uma tela de ajuda, que explica o funcionamento geral do jogo. Criei quatro ``Labels`` com o mesmo background que as ``Labels`` da classe ``Question`` possuem. Criei um ``ButtonGame`` que retorna ao menu e adicionei os quatro personagens na parte inferior da tela, porém apenas com fins estéticos. 

````java
public HelpScreen(MainGame game) {
        // Chama GameScreen carregando atlas e background de seleção
        super(game, "assets/assets.atlas", "Background_Select");

        // Botão para retornar ao menu
        this.returnGame = new ButtonGame("Retornar ao menu", 1.5f);

        // Fonte usada nos textos explicativos
        this.font = new BitmapFont(Gdx.files.internal("fonts/hud.fnt"));

        // Mundo Box2D (gravidade -10 no eixo Y)
        this.world = new World(new Vector2(0, -10), true);

        // O Stage será responsável por capturar input
        Gdx.input.setInputProcessor(stage);

        // Lista que armazenará os labels de ajuda
        this.helps = new ArrayList<Label>();

        // Lista que armazenará os personagens exibidos na tela
        this.characters = new ArrayList<Character>();

        // Textos que explicam como o jogo funciona
        String[] helpsText = {
                "Escolha um dentre quatro personagens disponíveis.",
                "Você deve responder 3 perguntas de nível fácil para enfrentar um chefe, onde deverá responder 3 perguntas difíceis, até vencer os 3 mapas.",
                "Em caso de erro, perderá vida, tendo 3 no total. A cada chefe derrotado, suas vidas regeneram.",
                "Sátiro possui perguntas de biologia, Samurai possui perguntas de história, Astrônomo de astronomia e Paladino possui perguntas de mitologia."
        };

        // Listener do botão: volta ao menu e libera a tela
        returnGame.getButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        // Espaçamento horizontal entre os personagens
        float spacing = WORLD_WIDTH / characterNames.length;

        // Cria e posiciona os 4 personagens exibidos no topo da tela
        for (int i = 0; i < characterNames.length; i++) {
            float x = spacing * (i + 0.5f); // posição centralizada por personagem
            characters.add(
                    new Character(
                            world,
                            "characters/" + characterNames[i] + ".atlas",
                            x,
                            46f, // altura fixa
                            0.2f // escala
                    ));
        }

        // Carrega background para os textos de ajuda
        Texture texture = new Texture(Gdx.files.internal("assets/background.png"));
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(texture));

        // Define estilo dos labels (fonte + background)
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;
        style.background = background;

        // Table organiza os elementos verticalmente
        Table table = new Table();
        table.setFillParent(true); // ocupa a tela toda
        table.top(); // alinha no topo

        // Cria cada label de ajuda e adiciona na table
        for (int index = 0; index < helpsText.length; index++) {
            table.row();
            final int i = index;

            Label help = new Label(helpsText[i], style);
            help.setWrap(true); // permite quebra de linha
            help.setAlignment(Align.center); // centraliza texto

            helps.add(help);
            table.add(helps.get(i))
                    .width(WORLD_WIDTH / 1.5f) // largura do bloco de texto
                    .padTop(50); // espaço entre linhas
        }

        // Adiciona o botão ao final
        table.row();
        table.add(returnGame.getButton()).size(560, 160).padTop(30);

        // Adiciona a table ao Stage
        stage.addActor(table);
}
````

## 4. Diagrama de Classes

<img src = "src/Diagrama.png" alt = "Diagrama de Classes">

## 5. Instrução de execução

Para executar localmente 

- Certifique-se de possuir:

1. Java JDK 8 ou superior;
2. Gradle;

- Clone o repositório: 
````
git clone https://github.com/elc117/gamification-2025b-julio.git
````

- Windows:
````
gradlew.bat lwjgl3:run
````
- Linux/MacOS:
````
gradlew lwjgl3:run
````

Para acessar online (recomendado): https://julio-mansan2.itch.io/busque-conhecimento

## 6. Demonstração do Programa

https://github.com/user-attachments/assets/5f93cf22-093d-4a4a-be17-48232c70cd50

## 7. Referências

https://www.youtube.com/playlist?list=PLZm85UZQLd2SXQzsF-a0-pPF6IWDDdrXt
https://www.spriters-resource.com/browse
https://craftpix.net
https://libgdx.com/wiki/
https://libgdx.com/wiki/extensions/physics/box2d
https://libgdx.com/wiki/graphics/2d/fonts/bitmap-fonts
https://libgdx.com/wiki/graphics/2d/scene2d/table
https://libgdx.com/wiki/graphics/2d/scene2d/skin
https://libgdx.com/wiki/graphics/2d/scene2d/scene2d-ui
https://libgdx.com/wiki/graphics/2d/2d-animation
https://libgdx.com/wiki/graphics/2d/orthographic-camera
https://libgdx.com/wiki/graphics/2d/spritebatch-textureregions-and-sprites
https://libgdx.com/wiki/graphics/2d/tile-maps
https://libgdx.com/wiki/tools/hiero
https://libgdx.com/wiki/tools/texture-packer

