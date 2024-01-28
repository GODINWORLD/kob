//map大小为13*14，每个格子的坐标可以用(x,y)表示，由于格子的大小会变化，所以在canvas里的坐标还要乘一个基准L，
//所以存的都是相对坐标，最后使用时还要乘L。
import { AcGameObject } from "./AcGameObject";
import { Cell } from "./Cell";

export class Snake extends AcGameObject{
    constructor(info, gamemap){
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;

        this.cells = [new Cell(info.r, info.c)];//存放蛇的身体， cell[0]存放蛇头
        this.next_cell = null; //下一步的目标位置

        this.speed = 5; //表示蛇每秒走5个格子,是速度

        this.direction = -1;//-1表示没有指令，0,1,2,3表示上右下左
        this.status = "idle";//idle表示静止，move正在移动，die死亡

        this.dr = [-1, 0, 1, 0];
        this.dc = [0, 1, 0, -1]; //列

        this.step = 0;//回合数
        this.eps = 1e-2; //误差

        this.eye_direction = 0;
        if (this.id === 1) this.eye_direction = 2; //左下角的蛇初始朝上，右上角的蛇初始朝下

        this.eye_dx = [ //蛇眼睛不同方向的x偏移量
            [-1, 1], //上右下左
            [1, 1],
            [1, -1],
            [-1, -1],        
        ]
        this.eye_dy = [ //蛇眼睛不同方向的y偏移量
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1],       
        ]
    }

    start(){

    }

    set_direction(d){
        this.direction = d;
    }

    check_tail_increasing(){ //检测当前回合，蛇的长度是否增加
        if(this.step <= 10) return true;//规则是前10个回合，每个回合长度加一，即蛇头动，蛇尾不动。
        if(this.step % 3 === 1) return true;//后面是每三个回合长度才加一
        return false;
    }

    next_step(){//将蛇的状态变为走下一步
        const d = this.direction;
        //一步走水平或垂直方向相对长度为1的距离
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.eye_direction = d;
        this.direction = -1; //清空
        this.status = "move";
        this.step ++;

        const k = this.cells.length;//求出数量
        for(let i = k; i > 0; i --){//可以用push或者直接修改length位置，都可以增加元素
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));//深层复制
            //由于每个元素都是对象引用，所以要一个个复制
        }
        //这时cells[0]和cell[1]是在同一个位置的，等下移动时，cell[0]动

        // if (!this.gamemap.check_valid(this.next_cell)){ //下一步操作撞了，蛇瞬间去世
        //     this.status = "die"; //这一步的判断逻辑交给后端
        // } 
    }

    update_move(){
        //这里感觉如果move_distance太长的话，有可能会超过目标点，这时dx,dy是负数，然后又回头，这个过程会很快，
        //用户应该看不出来
        const move_distance = this.speed * this.timedelta / 1000; //经过帧的间隔的时间，蛇头应该移动的距离md
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if(distance < this.eps){ //走到目标点了
            this.cells[0] = this.next_cell; //添加一个新蛇头
            this.next_cell = null;
            this.status = "idle";

            if(!this.check_tail_increasing())
            {
                this.cells.pop();//弹出蛇尾
            }
        }
        else {
            //一个回合next_step只会执行一次，也就是产生一个蛇头，在新蛇头没有移动到目标点时，也就是在帧与帧之间
            //移动时，新蛇头是不断得移向下一个点，蛇尾是不断的移向倒数第二个点，所以不能用pop，不然就把点全pop完了

            this.cells[0].x += move_distance * dx / distance;//用canvas坐标系，不用该r,c了
            this.cells[0].y += move_distance * dy / distance;//这是md在y方向上的投影长度

            if(! this.check_tail_increasing()){
                const k = this.cells.length;
                const tail = this.cells[k - 1], tail_target = this.cells[k - 2];
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;
                tail.y += move_distance * tail_dy / distance;
               // this.cells.pop(); //错误
            }
        }
    }

    update(){//每一帧执行一次
        if(this.status === 'move') {
            this.update_move();
        }

        this.render();
    }

    render(){
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;//先确定要画的图形的颜色
        if (this.status === "die"){
            ctx.fillStyle = "white";
        }

        for(const cell of this.cells){
            ctx.beginPath();//开始一条新的路径，确保每条路径都是独立的
            ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, Math.PI * 2);//圆心坐标，半径，角度
            ctx.fill();
        }

        //蛇身用圆形和矩形表示，矩形画在两个圆之间
        for(let i = 1; i < this.cells.length; i ++){
            const a = this.cells[i - 1], b = this.cells[i];
            if(Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps) continue;

            if(Math.abs(a.x - b.x) < this.eps){//垂直方向
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
            }
            else {
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            }
        }

        ctx.fillStyle = "black";//眼睛
        for (let i = 0; i < 2; i ++){
            const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.15) * L;//i是左眼右眼
            const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.15) * L;
            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L * 0.05, 0, Math.PI * 2);
            ctx.fill();
        }
    }   
}