import {Document, model, Schema} from "mongoose";
import {IBase, LogicDelete, schemaConfig, TBase} from "src/models/base";

export type TWidgetPos = {
    userId: string
    widgetId: string
    fixed: boolean
    left: number,
    top: number,
} & TBase;

export interface IWidgetPos extends TWidgetPos, Document {
}

const widgetPosSchema: Schema = new Schema(Object.assign({
    userId: {
        type: String,
        required: true,
    },
    widgetId: {
        type: String,
        required: true,
    },
    fixed: {
        type: Boolean,
        default: false,
        required: true
    },
    left: {
        type: Number,
        default: 64
    },
    top: {
        type: Number,
        default: 64
    },
}, IBase), schemaConfig);

/**
 * Mongoose Model based on TUser for TypeScript.
 * https://mongoosejs.com/docs/models.html
 *
 * TUser
 * @param email:string
 * @param password:string
 * @param avatar:string
 */

const WidgetPos = model<IWidgetPos, LogicDelete<IWidgetPos, {}>>("WidgetPos", widgetPosSchema);

export default WidgetPos;

