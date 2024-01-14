import { AcGameObject } from "./AcGameObject";

export class Wall extends AcGameObject{
    constructor(r, c, gamemap){
        super();

        this.r = r;
        this.c = c;
        this.gamemap = gamemap;
        this.color = "#B37226";
    }

    update(){
        this.render();
    }

    render(){
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        ctx.fillRect(this.c * L, this.r * L, L, L);
    }
}

//不需要写this
//canvas的x,y是笛卡尔坐标的，其他地方操作时按照正常的数组操作，到这里填充时再
//反着来即可