
import { AcGameObject } from "./AcGameObject";//引入的东西是export，加方括号，如果是
import { Snake } from "./Snake";
//export default，则不用方括号
import { Wall } from "./Wall";

export class GameMap extends AcGameObject{
    constructor(ctx, parent, store){//ctx是canvas, parent是playground
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.store = store;
        this.L = 0;

        this.rows = 13;
        this.cols = 14;//[11, 1] [1, 12]开始时二者x+y是偶数和奇数，后面也是类似，
        //保证不会同时走到同一个格子里

        this.inner_walls_count = 20;
        this.walls = [];

        this.snakes = [//两条蛇
            new Snake({id: 0, color: "#4876EC", r: this.rows - 2, c: 1}, this),
            new Snake({id: 1, color: "#F94848", r: 1, c: this.cols - 2}, this),
        ]; 

    }

    create_walls(){ //创建障碍物
        const g = this.store.state.pk.gamemap;//使用后端返回的地图

        for(let r = 0; r < this.rows; r ++){
            for(let c = 0; c < this.cols; c ++){
                if(g[r][c]){
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }

        return true;
    }

    add_listening_events(){ //对键盘进行监听
        this.ctx.canvas.focus();//聚焦

        this.ctx.canvas.addEventListener("keydown", e => {
            let d = -1;
            if(e.key === 'w') d = 0;
            else if(e.key === 'd') d = 1;
            else if(e.key === 's') d = 2;
            else if(e.key === 'a') d = 3;

            if (d >= 0){
                this.store.state.pk.socket.send(JSON.stringify({//给后端发消息，蛇动了，但是前端先不渲染画面
                    event: "move",
                    direction: d,
                }));
            }
        })
    }

    start(){
        this.create_walls();

        this.add_listening_events();
    }

    update_size(){//更新基准L和canvas大小
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        //L代表每个方格的边长
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    check_ready(){//检查两条蛇有没有准备好下一回合了
        for(const snake of this.snakes){
            if(snake.status !== "idle") return false;
            if(snake.direction === -1) return false;
        }
        return true;
    }

    next_step(){ //让两条蛇进入下一回合
        for (const snake of this.snakes){
            snake.next_step();
        }
    }

    // check_valid(cell){ // 检测目标位置是否合法：即没有撞到两条蛇的身体和障碍物
    //     for (const wall of this.walls){//先判断障碍物
    //         if(wall.r === cell.r && wall.c === cell.c){
    //             return false;
    //         }
    //     }

    //     for (const snake of this.snakes){
    //         let k = snake.cells.length;
    //         if (!snake.check_tail_increasing()){ //当蛇尾会前进的时候，蛇尾不要判断
    //             k --;//check_valid方法是在next_step方法调用的，这时还没有pop蛇尾
    //         }
    //         //由于定义了蛇头不会同时进入同一个格子，所以可以用以下方法判断一个蛇头将要碰到任何一个蛇身都会die
    //         //往回走也会die
    //         for (let i = 0; i < k; i ++){
    //             if (snake.cells[i].r === cell.r && snake.cells[i].c === cell.c){
    //                 return false;
    //             }
    //         }
    //     }

    //     return true;
    // }

    update(){
        this.update_size();
        if(this.check_ready()){//一开始监听了键盘，当两条蛇都从键盘读取到方向时，开始移动
            this.next_step();
        }

        this.render();
    }

    render(){
        const color_even = "#AAD751", color_odd = "#A2D149";
        for(let r = 0; r < this.rows; r ++){//画背景方格子
            for(let c = 0; c < this.cols; c ++){
                if((r + c) % 2 == 0) 
                {
                    this.ctx.fillStyle = color_even;
                }
                else this.ctx.fillStyle = color_odd;

                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }
}